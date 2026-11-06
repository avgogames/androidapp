import type { CellChanges } from '../model/coloringProgress';

/** One undoable step: each touched cell's painted value before and after. */
export interface Edit {
  readonly before: CellChanges;
  readonly after: CellChanges;
}

/**
 * Bounded undo/redo stacks. Each entry stores only the cells a stroke touched, so even a long
 * session costs a few kilobytes rather than a full progress snapshot per step.
 */
export class EditHistory {
  private readonly undoStack: Edit[] = [];
  private readonly redoStack: Edit[] = [];

  constructor(private readonly limit = 100) {}

  get canUndo(): boolean {
    return this.undoStack.length > 0;
  }

  get canRedo(): boolean {
    return this.redoStack.length > 0;
  }

  record(edit: Edit): void {
    if (edit.after.size === 0) return;
    this.undoStack.push(edit);
    if (this.undoStack.length > this.limit) this.undoStack.shift();
    this.redoStack.length = 0;
  }

  /** The changes that revert the last edit, or null when there is nothing to undo. */
  undo(): CellChanges | null {
    const edit = this.undoStack.pop();
    if (!edit) return null;
    this.redoStack.push(edit);
    return edit.before;
  }

  redo(): CellChanges | null {
    const edit = this.redoStack.pop();
    if (!edit) return null;
    this.undoStack.push(edit);
    return edit.after;
  }

  clear(): void {
    this.undoStack.length = 0;
    this.redoStack.length = 0;
  }
}
