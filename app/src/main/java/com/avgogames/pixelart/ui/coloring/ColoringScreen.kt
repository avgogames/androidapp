package com.avgogames.pixelart.ui.coloring

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ZoomOutMap
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avgogames.pixelart.R
import com.avgogames.pixelart.core.editor.Tool
import com.avgogames.pixelart.core.model.ColoringProgress
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.ui.components.PixelArtImage
import com.avgogames.pixelart.ui.components.SquareIconButton
import kotlin.math.roundToInt

/** Wiring: owns the ViewModel and forwards plain state and intents to [ColoringContent]. */
@Composable
fun ColoringRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ColoringViewModel = viewModel(factory = ColoringViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ColoringContent(
        state = state,
        actions = ColoringActions(
            onBack = onBack,
            onSelectColor = viewModel::selectColor,
            onSelectTool = viewModel::selectTool,
            onBrushSizeChange = viewModel::setBrushSize,
            onTap = viewModel::onTap,
            onStrokeStart = viewModel::onStrokeStart,
            onStrokeMove = viewModel::onStrokeMove,
            onStrokeEnd = viewModel::onStrokeEnd,
            onUndo = viewModel::undo,
            onRedo = viewModel::redo,
            onReset = viewModel::reset,
            onDismissCompletion = viewModel::dismissCompletion,
        ),
        modifier = modifier,
    )
}

/** Every intent the coloring screen can raise, grouped so previews and tests pass one object. */
class ColoringActions(
    val onBack: () -> Unit = {},
    val onSelectColor: (Int) -> Unit = {},
    val onSelectTool: (Tool) -> Unit = {},
    val onBrushSizeChange: (Int) -> Unit = {},
    val onTap: (Int) -> Unit = {},
    val onStrokeStart: () -> Unit = {},
    val onStrokeMove: (Int) -> Unit = {},
    val onStrokeEnd: () -> Unit = {},
    val onUndo: () -> Unit = {},
    val onRedo: () -> Unit = {},
    val onReset: () -> Unit = {},
    val onDismissCompletion: () -> Unit = {},
)

@Composable
fun ColoringContent(
    state: ColoringUiState,
    actions: ColoringActions,
    modifier: Modifier = Modifier,
    viewport: GridViewport = remember { GridViewport() },
) {
    val art = state.art
    val progress = state.progress
    var toolsOpen by rememberSaveable { mutableStateOf(false) }
    var confirmReset by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = toolsOpen) { toolsOpen = false }

    Column(modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBar(
            title = art?.title.orEmpty(),
            art = art,
            progress = progress,
            onBack = actions.onBack,
            onFit = viewport::reset,
            onReset = { confirmReset = true },
            modifier = Modifier.statusBarsPadding(),
        )

        Box(Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.notFound -> Text(
                    stringResource(R.string.coloring_not_found),
                    modifier = Modifier.align(Alignment.Center),
                )
                art == null || progress == null -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                else -> PixelGrid(
                    art = art,
                    progress = progress,
                    selectedColor = state.selectedColor,
                    tool = state.tool,
                    viewport = viewport,
                    onTap = actions.onTap,
                    onStrokeStart = actions.onStrokeStart,
                    onStrokeMove = actions.onStrokeMove,
                    onStrokeEnd = actions.onStrokeEnd,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            AnimatedVisibility(
                visible = toolsOpen,
                enter = slideInHorizontally { -it } + fadeIn(),
                exit = slideOutHorizontally { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
            ) {
                ToolPanel(
                    selected = state.tool,
                    onSelect = {
                        actions.onSelectTool(it)
                        toolsOpen = false
                    },
                    onClose = { toolsOpen = false },
                )
            }
        }

        if (art != null && progress != null) {
            BottomBar(
                art = art,
                progress = progress,
                state = state,
                actions = actions,
                onOpenTools = { toolsOpen = !toolsOpen },
            )
        }
    }

    if (confirmReset) {
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text(stringResource(R.string.coloring_reset_title)) },
            text = { Text(stringResource(R.string.coloring_reset_body)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmReset = false
                    viewport.reset()
                    actions.onReset()
                }) { Text(stringResource(R.string.coloring_reset_confirm), color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmReset = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }

    if (state.showCompletion && art != null) {
        CompletionDialog(
            art = art,
            onGallery = {
                actions.onDismissCompletion()
                actions.onBack()
            },
            onStay = {
                actions.onDismissCompletion()
                viewport.reset()
            },
        )
    }
}

@Composable
private fun TopBar(
    title: String,
    art: PixelArt?,
    progress: ColoringProgress?,
    onBack: () -> Unit,
    onFit: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        SquareIconButton(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.coloring_back), onBack)
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (art != null && progress != null) {
                Text(
                    stringResource(
                        R.string.coloring_progress,
                        (progress.fraction * 100).roundToInt(),
                        progress.correctCount,
                        progress.totalCount,
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        SquareIconButton(Icons.Filled.ZoomOutMap, stringResource(R.string.coloring_fit), onFit)
        SquareIconButton(
            Icons.Filled.RestartAlt,
            stringResource(R.string.coloring_reset),
            onReset,
            enabled = progress?.isStarted == true,
        )
    }
}

@Composable
private fun CompletionDialog(art: PixelArt, onGallery: () -> Unit, onStay: () -> Unit) {
    AlertDialog(
        onDismissRequest = onStay,
        title = { Text(stringResource(R.string.coloring_complete_title)) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PixelArtImage(
                    art = art,
                    progress = null,
                    revealAll = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(art.width.toFloat() / art.height)
                        .clip(RoundedCornerShape(12.dp)),
                )
                Spacer(Modifier.height(12.dp))
                Text(stringResource(R.string.coloring_complete_body, art.title))
            }
        },
        confirmButton = { Button(onClick = onGallery) { Text(stringResource(R.string.coloring_complete_gallery)) } },
        dismissButton = { TextButton(onClick = onStay) { Text(stringResource(R.string.coloring_complete_stay)) } },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}
