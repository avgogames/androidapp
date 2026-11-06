/**
 * Colors are opaque 24-bit RGB integers (0xRRGGBB). Keeping them as numbers makes palette math
 * and nearest-color matching cheap; UI code converts with toHex at the edge.
 */
export type Rgb = number;

export const red = (c: Rgb): number => (c >> 16) & 0xff;
export const green = (c: Rgb): number => (c >> 8) & 0xff;
export const blue = (c: Rgb): number => c & 0xff;

const clampByte = (v: number): number => (v < 0 ? 0 : v > 255 ? 255 : v | 0);

export const rgb = (r: number, g: number, b: number): Rgb =>
  (clampByte(r) << 16) | (clampByte(g) << 8) | clampByte(b);

/** Relative luminance in 0..1 (Rec. 709 weights on gamma-encoded channels; good enough for sorting). */
export const luminance = (c: Rgb): number =>
  (0.2126 * red(c) + 0.7152 * green(c) + 0.0722 * blue(c)) / 255;

/** Linear blend from a (t = 0) to b (t = 1). */
export const mix = (a: Rgb, b: Rgb, t: number): Rgb =>
  rgb(
    Math.round(red(a) + (red(b) - red(a)) * t),
    Math.round(green(a) + (green(b) - green(a)) * t),
    Math.round(blue(a) + (blue(b) - blue(a)) * t),
  );

export const toHex = (c: Rgb): string => '#' + (c & 0xffffff).toString(16).padStart(6, '0');

export function parseHex(hex: string): Rgb {
  const match = /^#?([0-9a-f]{6})$/i.exec(hex);
  if (!match) throw new Error(`Not a #rrggbb color: ${hex}`);
  return parseInt(match[1], 16);
}
