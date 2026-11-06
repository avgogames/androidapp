package com.avgogames.pixelart.core.editor

/** Coloring tools, mirroring the tool drawer in the editor reference screen. */
enum class Tool {
    /** Paints exactly the touched cell with the selected color; mistakes are allowed and shown. */
    PENCIL,

    /** Paints a square of the brush size cells but only where the number matches. */
    BRUSH,

    /** Fills every connected cell sharing the tapped cell's number. */
    BUCKET,

    /** Clears painted cells in a the brush size square. */
    ERASER,

    /** Selects the tapped cell's number, then returns to the previous tool. */
    PICKER,

    /** One-finger panning; painting tools pan with two fingers. */
    HAND;

    /** Whether a one-finger drag paints (true) or pans the canvas (false). */
    val paintsOnDrag: Boolean get() = this == PENCIL || this == BRUSH || this == ERASER

    val usesBrushSize: Boolean get() = this == BRUSH || this == ERASER
}
