import type { PixelArt } from './pixelArt';

export const UNPAINTED = -1;

/** cell -> new painted index (or UNPAINTED). */
export type CellChanges = ReadonlyMap<number, number>;

/**
 * What the player has painted on one artwork. Immutable: every edit returns a new instance,
 * which keeps undo/redo and React state snapshots trivial.
 *
 * `painted` holds, per cell, the palette index the player applied or UNPAINTED. A painted index
 * that differs from the artwork's target is a visible mistake.
 */
export class ColoringProgress {
  private constructor(
    readonly artId: string,
    private readonly painted: Int8Array,
    /** Count of cells whose painted index matches the target. */
    readonly correctCount: number,
    readonly totalCount: number,
  ) {}

  static empty(art: PixelArt): ColoringProgress {
    return new ColoringProgress(art.id, new Int8Array(art.cellCount).fill(UNPAINTED), 0, art.cellCount);
  }

  /** Rebuilds progress from stored cells, dropping any index the palette no longer has. */
  static restore(art: PixelArt, stored: ArrayLike<number>): ColoringProgress {
    if (stored.length !== art.cellCount) return ColoringProgress.empty(art);
    const painted = new Int8Array(stored.length);
    let correct = 0;
    for (let cell = 0; cell < stored.length; cell++) {
      const value = stored[cell];
      painted[cell] = Number.isInteger(value) && value >= 0 && value < art.palette.length ? value : UNPAINTED;
      if (painted[cell] === art.cells[cell]) correct++;
    }
    return new ColoringProgress(art.id, painted, correct, art.cellCount);
  }

  paintedAt(cell: number): number {
    return this.painted[cell];
  }

  isCorrect(art: PixelArt, cell: number): boolean {
    return this.painted[cell] === art.cells[cell];
  }

  get fraction(): number {
    return this.totalCount === 0 ? 0 : this.correctCount / this.totalCount;
  }

  get isComplete(): boolean {
    return this.totalCount > 0 && this.correctCount === this.totalCount;
  }

  get isStarted(): boolean {
    return this.painted.some((v) => v !== UNPAINTED);
  }

  /** Applies changes and returns the new progress, or `this` when nothing changes. */
  with(art: PixelArt, changes: CellChanges): ColoringProgress {
    if (changes.size === 0) return this;
    let copy: Int8Array | null = null;
    let correct = this.correctCount;
    for (const [cell, value] of changes) {
      const old = (copy ?? this.painted)[cell];
      if (old === value) continue;
      const target = art.cells[cell];
      if (old === target) correct--;
      if (value === target) correct++;
      copy ??= this.painted.slice();
      copy[cell] = value;
    }
    return copy ? new ColoringProgress(this.artId, copy, correct, this.totalCount) : this;
  }

  /** Count of correctly painted cells per palette index. */
  correctCountPerColor(art: PixelArt): number[] {
    const counts = new Array<number>(art.palette.length).fill(0);
    for (let cell = 0; cell < this.painted.length; cell++) {
      const target = art.cells[cell];
      if (this.painted[cell] === target) counts[target]++;
    }
    return counts;
  }

  paintedCopy(): Int8Array {
    return this.painted.slice();
  }

  equals(other: ColoringProgress): boolean {
    if (other.artId !== this.artId || other.painted.length !== this.painted.length) return false;
    for (let i = 0; i < this.painted.length; i++) if (other.painted[i] !== this.painted[i]) return false;
    return true;
  }
}
