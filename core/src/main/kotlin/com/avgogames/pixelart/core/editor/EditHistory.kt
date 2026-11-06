package com.avgogames.pixelart.core.editor

/**
 * Bounded undo/redo stacks. Each entry stores only the cells a stroke touched, so even a long
 * session costs a few kilobytes rather than a full progress snapshot per step.
 */
class EditHistory(private val limit: Int = 100) {

    /** One undoable step: each touched cell's painted value before and after. */
    class Edit(val before: Map<Int, Int>, val after: Map<Int, Int>)

    private val undoStack = ArrayDeque<Edit>()
    private val redoStack = ArrayDeque<Edit>()

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    fun record(edit: Edit) {
        if (edit.after.isEmpty()) return
        undoStack.addLast(edit)
        if (undoStack.size > limit) undoStack.removeFirst()
        redoStack.clear()
    }

    /** Returns the changes that revert the last edit, or null when there is nothing to undo. */
    fun undo(): Map<Int, Int>? {
        val edit = undoStack.removeLastOrNull() ?: return null
        redoStack.addLast(edit)
        return edit.before
    }

    fun redo(): Map<Int, Int>? {
        val edit = redoStack.removeLastOrNull() ?: return null
        undoStack.addLast(edit)
        return edit.after
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
