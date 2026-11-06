import { parseHex, toHex } from '../color';
import { isCategory } from '../model/category';
import { ColoringProgress, UNPAINTED } from '../model/coloringProgress';
import { createPixelArt, MAX_COLORS, type PixelArt } from '../model/pixelArt';

/**
 * Text encodings for storage. One character per cell keeps a 64x64 import or its progress
 * around 4 KB, the same budget as the Android app's one byte per cell.
 */
export const CELL_ALPHABET = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_';
const UNPAINTED_CHAR = '.';
const charIndex = new Map([...CELL_ALPHABET].map((c, i) => [c, i]));

if (CELL_ALPHABET.length < MAX_COLORS) throw new Error('Cell alphabet too small for the palette limit');

export function encodeCells(cells: ArrayLike<number>): string {
  let out = '';
  for (let i = 0; i < cells.length; i++) out += cells[i] === UNPAINTED ? UNPAINTED_CHAR : CELL_ALPHABET[cells[i]];
  return out;
}

export function decodeCells(text: string): number[] {
  const out = new Array<number>(text.length);
  for (let i = 0; i < text.length; i++) {
    const ch = text[i];
    const value = ch === UNPAINTED_CHAR ? UNPAINTED : charIndex.get(ch);
    if (value === undefined) throw new Error(`Bad cell character '${ch}'`);
    out[i] = value;
  }
  return out;
}

/** JSON shape of a stored or bundled artwork. */
export interface StoredArt {
  v: 1;
  id: string;
  title: string;
  category: string;
  width: number;
  height: number;
  palette: string[];
  cells: string;
}

export function artToJson(art: PixelArt): string {
  const stored: StoredArt = {
    v: 1,
    id: art.id,
    title: art.title,
    category: art.category,
    width: art.width,
    height: art.height,
    palette: art.palette.map(toHex),
    cells: encodeCells(art.cells),
  };
  return JSON.stringify(stored);
}

export function artFromStored(stored: Omit<StoredArt, 'v'>): PixelArt {
  if (!isCategory(stored.category)) throw new Error(`Unknown category ${stored.category}`);
  return createPixelArt({
    id: stored.id,
    title: stored.title,
    category: stored.category,
    width: stored.width,
    height: stored.height,
    palette: stored.palette.map(parseHex),
    cells: decodeCells(stored.cells),
  });
}

export function artFromJson(json: string): PixelArt {
  const parsed = JSON.parse(json) as StoredArt;
  if (parsed.v !== 1) throw new Error(`Unsupported art version ${String(parsed.v)}`);
  return artFromStored(parsed);
}

/** Progress is stored as `<cell count>:<cells>` so a size mismatch is caught on restore. */
export function progressToString(progress: ColoringProgress): string {
  return `${progress.totalCount}:${encodeCells(progress.paintedCopy())}`;
}

export function progressFromString(art: PixelArt, text: string): ColoringProgress {
  const sep = text.indexOf(':');
  if (sep < 0 || Number(text.slice(0, sep)) !== art.cellCount) return ColoringProgress.empty(art);
  return ColoringProgress.restore(art, decodeCells(text.slice(sep + 1)));
}
