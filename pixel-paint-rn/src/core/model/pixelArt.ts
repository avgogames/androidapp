import type { Rgb } from '../color';
import type { Category } from './category';

/** Upper bound keeps the palette bar usable and lets progress be stored in one byte per cell. */
export const MAX_COLORS = 48;

/**
 * An immutable color-by-number artwork. Every cell holds an index into `palette`; the player
 * paints a cell correctly by applying the palette color with the same index.
 */
export interface PixelArt {
  readonly id: string;
  readonly title: string;
  readonly category: Category;
  readonly width: number;
  readonly height: number;
  readonly palette: readonly Rgb[];
  /** Row-major palette indices. Treat as read-only. */
  readonly cells: Uint8Array;
  /** Number of cells that use each palette color, indexed like `palette`. */
  readonly cellCountPerColor: readonly number[];
  readonly cellCount: number;
}

export interface PixelArtInput {
  id: string;
  title: string;
  category: Category;
  width: number;
  height: number;
  palette: readonly Rgb[];
  cells: ArrayLike<number>;
}

/** Validates and freezes an artwork. Throws on inconsistent input, like the Kotlin model. */
export function createPixelArt(input: PixelArtInput): PixelArt {
  const { id, title, category, width, height, palette } = input;
  if (!(width > 0 && height > 0)) throw new Error(`Size must be positive, was ${width}x${height}`);
  if (input.cells.length !== width * height) {
    throw new Error(`Expected ${width * height} cells, got ${input.cells.length}`);
  }
  if (palette.length < 1 || palette.length > MAX_COLORS) {
    throw new Error(`Palette must have 1..${MAX_COLORS} colors, had ${palette.length}`);
  }
  const cells = Uint8Array.from(input.cells);
  const counts = new Array<number>(palette.length).fill(0);
  for (let i = 0; i < cells.length; i++) {
    const index = cells[i];
    if (index >= palette.length || input.cells[i] !== index) {
      throw new Error(`Cell color index ${input.cells[i]} outside palette`);
    }
    counts[index]++;
  }
  return Object.freeze({
    id,
    title,
    category,
    width,
    height,
    palette: Object.freeze([...palette]),
    cells,
    cellCountPerColor: Object.freeze(counts),
    cellCount: width * height,
  });
}
