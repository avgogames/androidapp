import { luminance, rgb, type Rgb } from '../color';
import { Category } from '../model/category';
import { createPixelArt, type PixelArt } from '../model/pixelArt';
import { nearestColor, quantize } from './colorQuantizer';

export const MIN_GRID = 16;
export const MAX_GRID = 64;
export const MIN_COLORS = 2;
export const MAX_IMPORT_COLORS = 32;

/** Settings the import screen exposes. */
export interface ConversionOptions {
  /** Columns in the resulting grid; rows follow the source aspect ratio. */
  gridWidth: number;
  /** Upper bound on palette size. Fewer colors are used when the image has fewer. */
  maxColors: number;
}

export const DEFAULT_OPTIONS: ConversionOptions = { gridWidth: 32, maxColors: 16 };

/** Decoded image: non-premultiplied RGBA bytes, 4 per pixel, row-major. */
export interface DecodedImage {
  width: number;
  height: number;
  rgba: ArrayLike<number>;
}

/**
 * Turns decoded image pixels into a color-by-number PixelArt: box-filter downscale to the grid,
 * composite transparency over white, quantize to a palette, then index every cell.
 */
export function convertToPixelArt(
  id: string,
  title: string,
  image: DecodedImage,
  options: ConversionOptions = DEFAULT_OPTIONS,
): PixelArt {
  const { width: sw, height: sh, rgba } = image;
  if (!(sw > 0 && sh > 0)) throw new Error('Empty image');
  if (rgba.length !== sw * sh * 4) throw new Error('Pixel buffer does not match size');
  if (options.gridWidth < MIN_GRID || options.gridWidth > MAX_GRID) {
    throw new Error(`gridWidth must be in ${MIN_GRID}..${MAX_GRID}`);
  }
  if (options.maxColors < MIN_COLORS || options.maxColors > MAX_IMPORT_COLORS) {
    throw new Error(`maxColors must be in ${MIN_COLORS}..${MAX_IMPORT_COLORS}`);
  }

  const width = Math.min(options.gridWidth, sw);
  const height = Math.min(MAX_GRID * 2, Math.max(1, Math.round((sh * width) / sw)));
  const small = downscale(rgba, sw, sh, width, height);

  const palette = quantize(small, options.maxColors)
    .map((c, i) => [c, i] as const)
    .sort((a, b) => luminance(a[0]) - luminance(b[0]) || a[1] - b[1])
    .map(([c]) => c);
  const cells = small.map((c) => nearestColor(c, palette));

  // Drop palette entries nothing maps to, keeping indices dense.
  const used = [...new Set(cells)].sort((a, b) => a - b);
  const remap = new Int32Array(palette.length).fill(-1);
  used.forEach((old, newIndex) => (remap[old] = newIndex));
  return createPixelArt({
    id,
    title,
    category: Category.IMPORTED,
    width,
    height,
    palette: used.map((i) => palette[i]),
    cells: cells.map((c) => remap[c]),
  });
}

/**
 * Area-average downscale: each output cell is the mean of the source pixels it covers, which
 * keeps thin features and avoids the shimmer of nearest-neighbour sampling.
 */
export function downscale(src: ArrayLike<number>, sw: number, sh: number, dw: number, dh: number): Rgb[] {
  const out = new Array<Rgb>(dw * dh);
  for (let oy = 0; oy < dh; oy++) {
    const y0 = Math.floor((oy * sh) / dh);
    const y1 = Math.max(y0 + 1, Math.floor(((oy + 1) * sh) / dh));
    for (let ox = 0; ox < dw; ox++) {
      const x0 = Math.floor((ox * sw) / dw);
      const x1 = Math.max(x0 + 1, Math.floor(((ox + 1) * sw) / dw));
      let r = 0;
      let g = 0;
      let b = 0;
      let n = 0;
      for (let y = y0; y < y1; y++) {
        for (let x = x0; x < x1; x++) {
          const p = (y * sw + x) * 4;
          const a = src[p + 3];
          // Composite over white so transparent PNG areas become paper, not black.
          r += Math.floor((src[p] * a + 255 * (255 - a)) / 255);
          g += Math.floor((src[p + 1] * a + 255 * (255 - a)) / 255);
          b += Math.floor((src[p + 2] * a + 255 * (255 - a)) / 255);
          n++;
        }
      }
      out[oy * dw + ox] = rgb(Math.floor(r / n), Math.floor(g / n), Math.floor(b / n));
    }
  }
  return out;
}
