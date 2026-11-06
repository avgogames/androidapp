import { ColoringProgress } from '../model/coloringProgress';
import type { PixelArt } from '../model/pixelArt';
import { ColoringEditor } from './coloringEditor';
import { EditHistory } from './editHistory';
import { MAX_BRUSH, MIN_BRUSH, Tool } from './tool';

/** Everything the coloring screen renders. A new object is emitted on every change. */
export interface ColoringState {
  readonly art: PixelArt;
  readonly progress: ColoringProgress;
  readonly selectedColor: number;
  readonly tool: Tool;
  readonly brushSize: number;
  readonly canUndo: boolean;
  readonly canRedo: boolean;
  readonly showCompletion: boolean;
}

/**
 * The coloring screen's state holder, kept free of React so the rules are unit-testable.
 * Strokes accumulate in memory and become one undoable edit when the finger lifts; `onCommit`
 * is called after every committed edit so the caller can persist it.
 */
export class ColoringSession {
  private state: ColoringState;
  private readonly editor: ColoringEditor;
  private readonly history = new EditHistory();
  private readonly listeners = new Set<() => void>();

  /** Cells touched by the stroke in progress, with their value before the stroke began. */
  private readonly strokeBefore = new Map<number, number>();
  private lastStrokeCell: number | null = null;
  private toolBeforePicker: Tool = Tool.PENCIL;

  constructor(
    art: PixelArt,
    progress: ColoringProgress,
    private readonly onCommit: (progress: ColoringProgress) => void,
  ) {
    this.editor = new ColoringEditor(art);
    this.state = {
      art,
      progress,
      selectedColor: nextUnfinishedColor(art, progress, art.palette.length - 1),
      tool: Tool.PENCIL,
      brushSize: 2,
      canUndo: false,
      canRedo: false,
      showCompletion: false,
    };
  }

  getState = (): ColoringState => this.state;

  subscribe = (listener: () => void): (() => void) => {
    this.listeners.add(listener);
    return () => this.listeners.delete(listener);
  };

  selectColor(index: number): void {
    const s = this.state;
    this.set({ selectedColor: index, tool: s.tool === Tool.PICKER ? this.toolBeforePicker : s.tool });
  }

  selectTool(tool: Tool): void {
    if (tool === Tool.PICKER && this.state.tool !== Tool.PICKER) this.toolBeforePicker = this.state.tool;
    this.set({ tool });
  }

  setBrushSize(size: number): void {
    this.set({ brushSize: Math.min(MAX_BRUSH, Math.max(MIN_BRUSH, Math.round(size))) });
  }

  /** A tap: tools that act on a single touch (bucket, picker) and one-cell strokes. */
  tap(cell: number): void {
    const { tool, art } = this.state;
    if (tool === Tool.PICKER) {
      this.set({ selectedColor: art.cells[cell], tool: this.toolBeforePicker });
    } else if (tool !== Tool.HAND) {
      this.strokeStart();
      this.strokeMove(cell);
      this.strokeEnd();
    }
  }

  strokeStart(): void {
    this.strokeBefore.clear();
    this.lastStrokeCell = null;
  }

  strokeMove(cell: number): void {
    const s = this.state;
    const path = this.lastStrokeCell === null ? [cell] : this.editor.cellsBetween(this.lastStrokeCell, cell);
    this.lastStrokeCell = cell;
    let next = s.progress;
    for (const step of path) {
      const changes = this.editor.changesFor(s.tool, step, s.selectedColor, s.brushSize, next);
      for (const changed of changes.keys()) {
        if (!this.strokeBefore.has(changed)) this.strokeBefore.set(changed, next.paintedAt(changed));
      }
      next = next.with(s.art, changes);
    }
    if (next !== s.progress) this.set({ progress: next });
  }

  strokeEnd(): void {
    const progress = this.state.progress;
    const before = new Map<number, number>();
    const after = new Map<number, number>();
    for (const [cell, value] of this.strokeBefore) {
      const now = progress.paintedAt(cell);
      if (now !== value) {
        before.set(cell, value);
        after.set(cell, now);
      }
    }
    this.strokeBefore.clear();
    this.lastStrokeCell = null;
    if (before.size === 0) return;
    this.history.record({ before, after });
    this.commit(progress, true);
  }

  undo(): void {
    this.applyHistory(this.history.undo());
  }

  redo(): void {
    this.applyHistory(this.history.redo());
  }

  /** Clears the artwork and its undo history. The caller deletes the stored progress. */
  reset(): void {
    this.history.clear();
    const empty = ColoringProgress.empty(this.state.art);
    this.set({ progress: empty, canUndo: false, canRedo: false, selectedColor: 0, showCompletion: false });
  }

  dismissCompletion(): void {
    this.set({ showCompletion: false });
  }

  private applyHistory(changes: ReadonlyMap<number, number> | null): void {
    if (!changes) return;
    const s = this.state;
    this.commit(s.progress.with(s.art, changes), false);
  }

  private commit(progress: ColoringProgress, celebrate: boolean): void {
    const s = this.state;
    let selected = s.selectedColor;
    // Hop to the next unfinished number once the current one is done.
    if (progress.correctCountPerColor(s.art)[selected] === s.art.cellCountPerColor[selected]) {
      selected = nextUnfinishedColor(s.art, progress, selected);
    }
    this.set({
      progress,
      selectedColor: selected,
      canUndo: this.history.canUndo,
      canRedo: this.history.canRedo,
      showCompletion: s.showCompletion || (celebrate && progress.isComplete),
    });
    this.onCommit(progress);
  }

  private set(patch: Partial<ColoringState>): void {
    this.state = { ...this.state, ...patch };
    this.listeners.forEach((l) => l());
  }
}

/** The first palette index after `from` (wrapping) that still has cells to paint. */
export function nextUnfinishedColor(art: PixelArt, progress: ColoringProgress, from: number): number {
  const done = progress.correctCountPerColor(art);
  const n = art.palette.length;
  for (let offset = 1; offset <= n; offset++) {
    const index = (from + offset) % n;
    if (done[index] < art.cellCountPerColor[index]) return index;
  }
  return from;
}
