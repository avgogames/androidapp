import { describe, expect, it } from '@jest/globals';
import { Category, convertToPixelArt, nearestColor, type DecodedImage } from '..';

function image(w: number, h: number, pixel: (x: number, y: number) => [number, number, number, number]): DecodedImage {
  const rgba = new Uint8Array(w * h * 4);
  for (let y = 0; y < h; y++) for (let x = 0; x < w; x++) rgba.set(pixel(x, y), (y * w + x) * 4);
  return { width: w, height: h, rgba };
}

const gradient = (w: number, h: number) =>
  image(w, h, (x, y) => [Math.floor((x * 255) / w), Math.floor((y * 255) / h), 0x40, 255]);

describe('convertToPixelArt', () => {
  it('keeps aspect ratio and grid width', () => {
    const art = convertToPixelArt('i', 'Photo', gradient(400, 200), { gridWidth: 40, maxColors: 16 });
    expect([art.width, art.height]).toEqual([40, 20]);
    expect(art.category).toBe(Category.IMPORTED);
  });

  it('respects the color limit and uses every color', () => {
    const art = convertToPixelArt('i', 'Photo', gradient(300, 300), { gridWidth: 32, maxColors: 8 });
    expect(art.palette.length).toBeLessThanOrEqual(8);
    expect(art.cellCountPerColor.every((n) => n > 0)).toBe(true);
  });

  it('keeps exact flat colors', () => {
    const art = convertToPixelArt('i', 'Flag', image(64, 64, (x) => (x < 32 ? [255, 0, 0, 255] : [0, 0, 255, 255])), {
      gridWidth: 16,
      maxColors: 4,
    });
    expect(new Set(art.palette)).toEqual(new Set([0xff0000, 0x0000ff]));
    expect(art.palette[art.cells[0]]).toBe(0xff0000);
    expect(art.palette[art.cells[15]]).toBe(0x0000ff);
  });

  it('turns transparency into white paper', () => {
    const art = convertToPixelArt('i', 'Empty', image(32, 32, () => [0, 0, 0, 0]), { gridWidth: 16, maxColors: 4 });
    expect(art.palette).toEqual([0xffffff]);
  });

  it('does not upscale small images', () => {
    expect(convertToPixelArt('i', 'Tiny', gradient(20, 10), { gridWidth: 48, maxColors: 16 }).width).toBe(20);
  });

  it('matches nearest colors perceptually', () => {
    const palette = [0x000000, 0xffffff, 0xff0000];
    expect(nearestColor(0xe01010, palette)).toBe(2);
    expect(nearestColor(0xeeeeee, palette)).toBe(1);
  });
});
