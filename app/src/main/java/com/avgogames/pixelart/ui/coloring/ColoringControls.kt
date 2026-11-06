package com.avgogames.pixelart.ui.coloring

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Colorize
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.avgogames.pixelart.R
import com.avgogames.pixelart.core.editor.Tool
import com.avgogames.pixelart.core.model.ColoringProgress
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.core.raster.luminance
import com.avgogames.pixelart.ui.components.PixelIcons
import com.avgogames.pixelart.ui.components.SquareIconButton
import com.avgogames.pixelart.ui.theme.PixelColors
import kotlin.math.roundToInt

val Tool.icon: ImageVector
    get() = when (this) {
        Tool.PENCIL -> Icons.Filled.Edit
        Tool.BRUSH -> Icons.Filled.Brush
        Tool.BUCKET -> Icons.Filled.FormatColorFill
        Tool.ERASER -> PixelIcons.Eraser
        Tool.PICKER -> Icons.Filled.Colorize
        Tool.HAND -> Icons.Filled.PanTool
    }

@Composable
fun Tool.label(): String = stringResource(
    when (this) {
        Tool.PENCIL -> R.string.tool_pencil
        Tool.BRUSH -> R.string.tool_brush
        Tool.BUCKET -> R.string.tool_bucket
        Tool.ERASER -> R.string.tool_eraser
        Tool.PICKER -> R.string.tool_picker
        Tool.HAND -> R.string.tool_hand
    },
)

/** Floating tool drawer modelled on the reference editor's "TOOLS" card. */
@Composable
fun ToolPanel(
    selected: Tool,
    onSelect: (Tool) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.width(224.dp),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 12.dp,
    ) {
        Column(Modifier.padding(vertical = 12.dp)) {
            Text(
                stringResource(R.string.coloring_tools).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = PixelColors.InkMuted,
                modifier = Modifier.padding(start = 20.dp, bottom = 8.dp),
            )
            for (tool in Tool.entries) {
                val isSelected = tool == selected
                Surface(
                    onClick = { onSelect(tool) },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (isSelected) Color.White else PixelColors.Ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                        .semantics { this.selected = isSelected },
                ) {
                    Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(tool.icon, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(16.dp))
                        Text(tool.label(), style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(PixelColors.Outline))
            Surface(
                onClick = onClose,
                color = Color.Transparent,
                contentColor = PixelColors.InkMuted,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    Modifier.padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.coloring_close).uppercase(), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

/** Tool button, current color, brush size, undo/redo, then the numbered palette. */
@Composable
fun BottomBar(
    art: PixelArt,
    progress: ColoringProgress,
    state: ColoringUiState,
    actions: ColoringActions,
    onOpenTools: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp),
        shadowElevation = 12.dp,
    ) {
        Column(Modifier.navigationBarsPadding().padding(top = 12.dp, bottom = 8.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                SquareIconButton(state.tool.icon, state.tool.label(), onOpenTools, selected = true)
                SelectedColorChip(art, state.selectedColor)
                BrushSizeSlider(
                    size = state.brushSize,
                    enabled = state.tool.usesBrushSize,
                    onChange = actions.onBrushSizeChange,
                    modifier = Modifier.weight(1f),
                )
                SquareIconButton(
                    Icons.AutoMirrored.Filled.Undo,
                    stringResource(R.string.coloring_undo),
                    actions.onUndo,
                    enabled = state.canUndo,
                )
                SquareIconButton(
                    Icons.AutoMirrored.Filled.Redo,
                    stringResource(R.string.coloring_redo),
                    actions.onRedo,
                    enabled = state.canRedo,
                )
            }
            Spacer(Modifier.height(12.dp))
            PaletteStrip(
                art = art,
                progress = progress,
                selected = state.selectedColor,
                onSelect = actions.onSelectColor,
            )
        }
    }
}

@Composable
private fun SelectedColorChip(art: PixelArt, selected: Int, modifier: Modifier = Modifier) {
    val color = Color(art.palette[selected])
    val description = stringResource(R.string.coloring_selected_color, selected + 1)
    Box(
        modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, PixelColors.Outline, RoundedCornerShape(12.dp))
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier.size(32.dp).clip(CircleShape).background(color).border(1.dp, PixelColors.Outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "${selected + 1}",
                style = MaterialTheme.typography.labelSmall,
                color = contrastingInk(art.palette[selected]),
            )
        }
    }
}

@Composable
private fun BrushSizeSlider(size: Int, enabled: Boolean, onChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.coloring_brush_size)
    Surface(
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, PixelColors.Outline),
    ) {
        Row(
            Modifier.fillMaxHeight().padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Slider(
                value = size.toFloat(),
                onValueChange = { onChange(it.roundToInt()) },
                valueRange = ColoringViewModel.MIN_BRUSH.toFloat()..ColoringViewModel.MAX_BRUSH.toFloat(),
                steps = ColoringViewModel.MAX_BRUSH - ColoringViewModel.MIN_BRUSH - 1,
                enabled = enabled,
                colors = SliderDefaults.colors(
                    thumbColor = PixelColors.Ink,
                    activeTrackColor = PixelColors.Ink,
                    inactiveTrackColor = PixelColors.Outline,
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent,
                ),
                modifier = Modifier.weight(1f).semantics { contentDescription = description },
            )
            Text(
                "$size",
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) PixelColors.Ink else PixelColors.InkMuted.copy(alpha = 0.5f),
                modifier = Modifier.padding(start = 6.dp),
            )
        }
    }
}

/** Numbered swatches; finished colors show a check and step aside visually. */
@Composable
private fun PaletteStrip(
    art: PixelArt,
    progress: ColoringProgress,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val done = progress.correctCountPerColor(art)
    // Keep the active swatch in view when the ViewModel auto-advances to the next color.
    LaunchedEffect(selected) {
        val visible = listState.layoutInfo.visibleItemsInfo
        if (visible.none { it.index == selected && it.offset >= 0 }) {
            listState.animateScrollToItem(maxOf(0, selected - 2))
        }
    }
    LazyRow(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        itemsIndexed(art.palette) { index, argb ->
            val finished = done[index] == art.cellCountPerColor[index]
            Swatch(
                number = index + 1,
                argb = argb,
                isSelected = index == selected,
                finished = finished,
                fraction = done[index].toFloat() / art.cellCountPerColor[index],
                onClick = { onSelect(index) },
            )
        }
    }
}

@Composable
private fun Swatch(
    number: Int,
    argb: Int,
    isSelected: Boolean,
    finished: Boolean,
    fraction: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val ink = contrastingInk(argb)
    val description = if (finished) {
        stringResource(R.string.coloring_color_done, number)
    } else {
        stringResource(R.string.coloring_selected_color, number)
    }
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = Color(argb),
        border = if (isSelected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, PixelColors.Outline),
        shadowElevation = if (isSelected) 4.dp else 0.dp,
        modifier = modifier
            .size(width = 44.dp, height = if (isSelected) 52.dp else 44.dp)
            .semantics {
                contentDescription = description
                selected = isSelected
            },
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (finished) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = ink.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
            } else {
                Text("$number", style = MaterialTheme.typography.labelLarge, color = ink)
            }
            if (!finished && fraction > 0f) {
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                        .fillMaxWidth(fraction)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(ink.copy(alpha = 0.6f)),
                )
            }
        }
    }
}

private fun contrastingInk(argb: Int): Color = if (luminance(argb) > 0.6f) PixelColors.Ink else Color.White
