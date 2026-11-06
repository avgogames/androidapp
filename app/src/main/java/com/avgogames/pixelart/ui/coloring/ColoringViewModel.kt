package com.avgogames.pixelart.ui.coloring

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.avgogames.pixelart.core.data.ArtRepository
import com.avgogames.pixelart.core.editor.ColoringEditor
import com.avgogames.pixelart.core.editor.EditHistory
import com.avgogames.pixelart.core.editor.Tool
import com.avgogames.pixelart.core.model.ColoringProgress
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.ui.appContainer
import com.avgogames.pixelart.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Everything the coloring screen renders. [art] and [progress] are null while loading. */
data class ColoringUiState(
    val art: PixelArt? = null,
    val progress: ColoringProgress? = null,
    val selectedColor: Int = 0,
    val tool: Tool = Tool.PENCIL,
    val brushSize: Int = 2,
    val canUndo: Boolean = false,
    val canRedo: Boolean = false,
    val showCompletion: Boolean = false,
    val notFound: Boolean = false,
)

/**
 * Screen holder for one artwork. Strokes are accumulated in memory and committed as a single
 * undoable edit when the finger lifts; progress is persisted after every committed edit.
 */
class ColoringViewModel(
    savedState: SavedStateHandle,
    private val repository: ArtRepository,
) : ViewModel() {

    private val artId: String = checkNotNull(savedState[Routes.ART_ID])
    private val _uiState = MutableStateFlow(ColoringUiState())
    val uiState: StateFlow<ColoringUiState> = _uiState.asStateFlow()

    private var editor: ColoringEditor? = null
    private val history = EditHistory()

    /** Cells touched by the stroke in progress, with their value before the stroke began. */
    private val strokeBefore = linkedMapOf<Int, Int>()
    private var lastStrokeCell: Int? = null
    private var toolBeforePicker = Tool.PENCIL

    init {
        // Loading is owned by the ViewModel's lifetime: it must finish once per screen
        // instance regardless of recomposition or collectors coming and going.
        viewModelScope.launch {
            val art = repository.art(artId)
            if (art == null) {
                _uiState.update { it.copy(notFound = true) }
                return@launch
            }
            editor = ColoringEditor(art)
            val progress = repository.progress(art)
            _uiState.update {
                it.copy(art = art, progress = progress, selectedColor = firstUnfinishedColor(art, progress))
            }
        }
    }

    fun selectColor(index: Int) {
        _uiState.update { it.copy(selectedColor = index, tool = if (it.tool == Tool.PICKER) toolBeforePicker else it.tool) }
    }

    fun selectTool(tool: Tool) {
        _uiState.update {
            if (tool == Tool.PICKER && it.tool != Tool.PICKER) toolBeforePicker = it.tool
            it.copy(tool = tool)
        }
    }

    fun setBrushSize(size: Int) {
        _uiState.update { it.copy(brushSize = size.coerceIn(MIN_BRUSH, MAX_BRUSH)) }
    }

    /** A tap: tools that act on a single touch (bucket, picker) and one-cell strokes. */
    fun onTap(cell: Int) {
        val state = _uiState.value
        val art = state.art ?: return
        when (state.tool) {
            Tool.PICKER -> {
                _uiState.update { it.copy(selectedColor = art.targetAt(cell), tool = toolBeforePicker) }
            }
            Tool.HAND -> Unit
            else -> {
                onStrokeStart()
                onStrokeMove(cell)
                onStrokeEnd()
            }
        }
    }

    fun onStrokeStart() {
        strokeBefore.clear()
        lastStrokeCell = null
    }

    fun onStrokeMove(cell: Int) {
        val editor = editor ?: return
        val state = _uiState.value
        val art = state.art ?: return
        val progress = state.progress ?: return
        val path = lastStrokeCell?.let { editor.cellsBetween(it, cell) } ?: listOf(cell)
        lastStrokeCell = cell
        var next = progress
        for (step in path) {
            val changes = editor.changesFor(state.tool, step, state.selectedColor, state.brushSize, next)
            for (changed in changes.keys) strokeBefore.getOrPut(changed) { next.paintedAt(changed) }
            next = next.with(art, changes)
        }
        if (next !== progress) _uiState.value = state.copy(progress = next)
    }

    fun onStrokeEnd() {
        val progress = _uiState.value.progress ?: return
        val after = strokeBefore.keys.associateWith { progress.paintedAt(it) }
        val before = strokeBefore.filter { (cell, value) -> after[cell] != value }
        strokeBefore.clear()
        lastStrokeCell = null
        if (before.isEmpty()) return
        history.record(EditHistory.Edit(before, after.filterKeys { it in before }))
        commit(progress, celebrate = true)
    }

    fun undo() = applyHistory(history.undo())

    fun redo() = applyHistory(history.redo())

    fun reset() {
        val art = _uiState.value.art ?: return
        history.clear()
        viewModelScope.launch {
            val empty = repository.resetProgress(art)
            _uiState.update {
                it.copy(progress = empty, canUndo = false, canRedo = false, selectedColor = 0, showCompletion = false)
            }
        }
    }

    fun dismissCompletion() {
        _uiState.update { it.copy(showCompletion = false) }
    }

    private fun applyHistory(changes: Map<Int, Int>?) {
        if (changes == null) return
        val state = _uiState.value
        val art = state.art ?: return
        val progress = state.progress ?: return
        commit(progress.with(art, changes), celebrate = false)
    }

    private fun commit(progress: ColoringProgress, celebrate: Boolean) {
        val wasComplete = _uiState.value.showCompletion
        _uiState.update {
            var selected = it.selectedColor
            val art = it.art
            // Hop to the next unfinished number once the current one is done.
            if (art != null && progress.correctCountPerColor(art)[selected] == art.cellCountPerColor[selected]) {
                selected = nextUnfinishedColor(art, progress, selected)
            }
            it.copy(
                progress = progress,
                selectedColor = selected,
                canUndo = history.canUndo,
                canRedo = history.canRedo,
                showCompletion = wasComplete || (celebrate && progress.isComplete),
            )
        }
        viewModelScope.launch { repository.saveProgress(progress) }
    }

    private fun firstUnfinishedColor(art: PixelArt, progress: ColoringProgress): Int =
        nextUnfinishedColor(art, progress, art.palette.size - 1)

    private fun nextUnfinishedColor(art: PixelArt, progress: ColoringProgress, from: Int): Int {
        val done = progress.correctCountPerColor(art)
        for (offset in 1..art.palette.size) {
            val index = (from + offset) % art.palette.size
            if (done[index] < art.cellCountPerColor[index]) return index
        }
        return from
    }

    companion object {
        const val MIN_BRUSH = 1
        const val MAX_BRUSH = 5

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { ColoringViewModel(createSavedStateHandle(), appContainer().repository) }
        }
    }
}
