package com.avgogames.pixelart.ui.importer

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.avgogames.pixelart.core.convert.ConversionOptions
import com.avgogames.pixelart.core.convert.PixelArtConverter
import com.avgogames.pixelart.core.data.ArtRepository
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.data.DecodedImage
import com.avgogames.pixelart.data.ImageLoader
import com.avgogames.pixelart.ui.appContainer
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ImportUiState(
    val hasImage: Boolean = false,
    val preview: PixelArt? = null,
    val gridWidth: Int = 32,
    val maxColors: Int = 16,
    val title: String = "",
    val isWorking: Boolean = false,
    val isSaving: Boolean = false,
    val error: Boolean = false,
) {
    val canSave: Boolean get() = preview != null && !isWorking && !isSaving
}

/**
 * Decodes the picked photo once, then re-runs the conversion whenever grid size or color count
 * change. Slider drags are debounced and older conversions cancelled, so only the latest
 * settings ever produce a preview.
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ImportViewModel(
    private val repository: ArtRepository,
    private val imageLoader: ImageLoader,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportUiState())
    val uiState: StateFlow<ImportUiState> = _uiState.asStateFlow()

    private val source = MutableStateFlow<DecodedImage?>(null)

    /** One consumer (the screen) navigates once; buffered so a save during a config change is not lost. */
    private val created = Channel<String>(Channel.BUFFERED)
    val createdArtIds: Flow<String> = created.receiveAsFlow()

    init {
        // The preview pipeline lives exactly as long as this screen's ViewModel.
        combine(
            source.filterNotNull(),
            _uiState.mapLatestOptions(),
        ) { image, options -> image to options }
            .debounce(150)
            .onEach { _uiState.update { it.copy(isWorking = true) } }
            .mapLatest { (image, options) ->
                withContext(Dispatchers.Default) {
                    PixelArtConverter.convert(PREVIEW_ID, "", image.pixels, image.width, image.height, options)
                }
            }
            .onEach { art -> _uiState.update { it.copy(preview = art, isWorking = false) } }
            .launchIn(viewModelScope)
    }

    fun onImagePicked(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true, error = false) }
            try {
                source.value = imageLoader.load(uri)
                _uiState.update { it.copy(hasImage = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(isWorking = false, error = true) }
            }
        }
    }

    fun onGridWidthChange(width: Int) = _uiState.update {
        it.copy(gridWidth = width.coerceIn(ConversionOptions.MIN_GRID, ConversionOptions.MAX_GRID))
    }

    fun onMaxColorsChange(colors: Int) = _uiState.update {
        it.copy(maxColors = colors.coerceIn(MIN_COLORS, ConversionOptions.MAX_COLORS))
    }

    fun onTitleChange(title: String) = _uiState.update { it.copy(title = title.take(MAX_TITLE)) }

    fun save(defaultTitle: String) {
        val state = _uiState.value
        val preview = state.preview ?: return
        if (!state.canSave) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val id = "import_${System.currentTimeMillis()}"
            val title = state.title.trim().ifEmpty { defaultTitle }
            val art = PixelArt(id, title, preview.category, preview.width, preview.height, preview.palette, preview.cellsCopy())
            repository.saveImported(art)
            _uiState.update { it.copy(isSaving = false) }
            created.send(id)
        }
    }

    private fun StateFlow<ImportUiState>.mapLatestOptions(): Flow<ConversionOptions> =
        map { ConversionOptions(it.gridWidth, it.maxColors) }.distinctUntilChanged()

    companion object {
        private const val PREVIEW_ID = "preview"
        const val MIN_COLORS = 4
        private const val MAX_TITLE = 40

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                ImportViewModel(container.repository, container.imageLoader)
            }
        }
    }
}
