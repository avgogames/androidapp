package com.avgogames.pixelart.ui.coloring

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avgogames.pixelart.core.editor.Tool
import com.avgogames.pixelart.core.model.ColoringProgress
import com.avgogames.pixelart.core.model.PixelArt
import com.avgogames.pixelart.ui.components.unpaintedTone
import com.avgogames.pixelart.ui.theme.PixelColors
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

/**
 * Zoom and pan of the canvas. Plain UI state owned by composition: it is pure view mechanics,
 * so it never travels to the ViewModel. Values are read only in the draw and input phases,
 * so pinch-zooming redraws the canvas without recomposing the screen.
 */
@Stable
class GridViewport {
    var scale by mutableFloatStateOf(1f)
        private set
    var offset by mutableStateOf(Offset.Zero)
        private set

    fun reset() {
        scale = 1f
        offset = Offset.Zero
    }

    /** Cell size and top-left of the artwork for a canvas of [size]. */
    fun layout(size: Size, art: PixelArt): GridLayout {
        val base = min(size.width / art.width, size.height / art.height) * FIT_MARGIN
        val cell = base * scale
        val origin = Offset(
            (size.width - cell * art.width) / 2f + offset.x,
            (size.height - cell * art.height) / 2f + offset.y,
        )
        return GridLayout(cell, origin)
    }

    fun transform(size: Size, art: PixelArt, centroid: Offset, zoom: Float, pan: Offset, minCellPx: Float) {
        val before = layout(size, art)
        val base = before.cell / scale
        val maxScale = max(MIN_MAX_SCALE, minCellPx * 1.6f / base)
        val newScale = (scale * zoom).coerceIn(1f, maxScale)
        // Keep the point under the fingers fixed while zooming.
        val ratio = newScale / scale
        val newOrigin = centroid - (centroid - before.origin) * ratio + pan
        scale = newScale
        val centred = Offset(
            (size.width - base * newScale * art.width) / 2f,
            (size.height - base * newScale * art.height) / 2f,
        )
        offset = clampOffset(newOrigin - centred, size, base * newScale, art)
    }

    private fun clampOffset(raw: Offset, size: Size, cell: Float, art: PixelArt): Offset {
        val limitX = max(0f, (cell * art.width - size.width) / 2f) + size.width * 0.35f
        val limitY = max(0f, (cell * art.height - size.height) / 2f) + size.height * 0.35f
        return Offset(raw.x.coerceIn(-limitX, limitX), raw.y.coerceIn(-limitY, limitY))
    }

    private companion object {
        const val FIT_MARGIN = 0.94f
        const val MIN_MAX_SCALE = 3f
    }
}

class GridLayout(val cell: Float, val origin: Offset) {
    /** Row-major cell index under [point], or null outside the artwork. */
    fun cellAt(point: Offset, art: PixelArt): Int? {
        val x = floor((point.x - origin.x) / cell).toInt()
        val y = floor((point.y - origin.y) / cell).toInt()
        return if (x in 0 until art.width && y in 0 until art.height) y * art.width + x else null
    }
}

/**
 * The paintable canvas. One finger paints with drawing tools (or pans with the hand tool),
 * two fingers always zoom and pan. A touch that never passes the touch slop is a tap.
 */
@Composable
fun PixelGrid(
    art: PixelArt,
    progress: ColoringProgress,
    selectedColor: Int,
    tool: Tool,
    viewport: GridViewport,
    onTap: (Int) -> Unit,
    onStrokeStart: () -> Unit,
    onStrokeMove: (Int) -> Unit,
    onStrokeEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val touchSlop = LocalViewConfiguration.current.touchSlop
    val numberThresholdPx = with(density) { 14.dp.toPx() }
    val gridThresholdPx = with(density) { 7.dp.toPx() }
    val comfortableCellPx = with(density) { 36.dp.toPx() }

    val paletteColors = remember(art) { art.palette.map { Color(it) } }
    val paperColors = remember(art) { art.palette.map { unpaintedTone(it) } }
    val highlightColors = remember(art) { paperColors.map { lerp(it, PixelColors.CellHighlight, 0.65f) } }
    val textMeasurer = rememberTextMeasurer(cacheSize = 64)
    val labels = remember(textMeasurer) { NumberLabels(textMeasurer) }

    val currentTool by rememberUpdatedState(tool)
    val tap by rememberUpdatedState(onTap)
    val strokeStart by rememberUpdatedState(onStrokeStart)
    val strokeMove by rememberUpdatedState(onStrokeMove)
    val strokeEnd by rememberUpdatedState(onStrokeEnd)

    Canvas(
        modifier
            .clipToBounds()
            .pointerInput(art, viewport) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val start = down.position
                    val canvasSize = Size(size.width.toFloat(), size.height.toFloat())
                    var multiTouch = false
                    var pastSlop = false
                    var stroking = false
                    do {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.count { it.pressed }
                        if (pressed >= 2 && !multiTouch) {
                            multiTouch = true
                            if (stroking) { strokeEnd(); stroking = false }
                        }
                        val drawing = currentTool.paintsOnDrag && !multiTouch
                        if (!drawing) {
                            val pan = event.calculatePan()
                            val zoom = if (multiTouch) event.calculateZoom() else 1f
                            if (multiTouch || pastSlop || (event.changes.first().position - start).getDistance() > touchSlop) {
                                pastSlop = true
                                viewport.transform(canvasSize, art, event.calculateCentroid(), zoom, pan, comfortableCellPx)
                            }
                        } else {
                            val change = event.changes.first()
                            if (!pastSlop && (change.position - start).getDistance() > touchSlop) {
                                pastSlop = true
                                strokeStart()
                                stroking = true
                                viewport.layout(canvasSize, art).cellAt(start, art)?.let(strokeMove)
                            }
                            if (stroking && change.positionChange() != Offset.Zero) {
                                viewport.layout(canvasSize, art).cellAt(change.position, art)?.let(strokeMove)
                            }
                        }
                        event.changes.forEach { if (it.positionChange() != Offset.Zero) it.consume() }
                    } while (event.changes.any { it.pressed })

                    if (stroking) strokeEnd()
                    if (!pastSlop && !multiTouch) {
                        viewport.layout(canvasSize, art).cellAt(start, art)?.let(tap)
                    }
                }
            },
    ) {
        val layout = viewport.layout(size, art)
        val cell = layout.cell
        val origin = layout.origin
        val firstX = max(0, floor(-origin.x / cell).toInt())
        val firstY = max(0, floor(-origin.y / cell).toInt())
        val lastX = min(art.width - 1, floor((size.width - origin.x) / cell).toInt())
        val lastY = min(art.height - 1, floor((size.height - origin.y) / cell).toInt())
        val complete = progress.isComplete
        val showNumbers = !complete && cell >= numberThresholdPx
        val cellSize = Size(cell + 0.5f, cell + 0.5f)

        for (y in firstY..lastY) for (x in firstX..lastX) {
            val index = y * art.width + x
            val target = art.targetAt(index)
            val painted = progress.paintedAt(index)
            val color = when {
                painted == target -> paletteColors[target]
                painted != ColoringProgress.UNPAINTED -> lerp(paperColors[target], paletteColors[painted], 0.55f)
                target == selectedColor -> highlightColors[target]
                else -> paperColors[target]
            }
            drawRect(color, Offset(origin.x + x * cell, origin.y + y * cell), cellSize)
        }

        if (!complete && cell >= gridThresholdPx) {
            val stroke = max(1f, cell / 40f)
            for (x in firstX..lastX + 1) {
                val px = origin.x + x * cell
                drawLine(PixelColors.GridLine, Offset(px, origin.y + firstY * cell), Offset(px, origin.y + (lastY + 1) * cell), stroke)
            }
            for (y in firstY..lastY + 1) {
                val py = origin.y + y * cell
                drawLine(PixelColors.GridLine, Offset(origin.x + firstX * cell, py), Offset(origin.x + (lastX + 1) * cell, py), stroke)
            }
        }

        if (showNumbers) {
            val fontPx = cell * 0.42f
            for (y in firstY..lastY) for (x in firstX..lastX) {
                val index = y * art.width + x
                val target = art.targetAt(index)
                val painted = progress.paintedAt(index)
                if (painted == target) continue
                val label = labels.get(target + 1, fontPx, density.fontScale, density.density)
                val wrong = painted != ColoringProgress.UNPAINTED
                drawText(
                    textLayoutResult = label,
                    color = if (wrong) PixelColors.Warning else PixelColors.Ink.copy(alpha = if (target == selectedColor) 0.9f else 0.55f),
                    topLeft = Offset(
                        origin.x + x * cell + (cell - label.size.width) / 2f,
                        origin.y + y * cell + (cell - label.size.height) / 2f,
                    ),
                )
            }
        }
    }
}

/**
 * Caches measured number labels for the current font size. Font size is bucketed to whole
 * pixels so a pinch re-measures at most once per pixel of growth, not once per frame per cell.
 */
private class NumberLabels(private val measurer: TextMeasurer) {
    private var bucket = -1
    private val cache = HashMap<Int, TextLayoutResult>()

    fun get(number: Int, fontPx: Float, fontScale: Float, density: Float): TextLayoutResult {
        val px = fontPx.toInt().coerceAtLeast(6)
        if (px != bucket) {
            cache.clear()
            bucket = px
        }
        return cache.getOrPut(number) {
            // Convert px to sp so labels track the cell, not the system font size.
            val sp = px / (density * fontScale)
            measurer.measure(number.toString(), TextStyle(fontSize = sp.sp, fontWeight = FontWeight.SemiBold))
        }
    }
}
