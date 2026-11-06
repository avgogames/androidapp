import { createPicture, Skia, type SkFont, type SkPaint, type SkPicture } from '@shopify/react-native-skia';
import { toHex, UNPAINTED, type ColoringProgress, type PixelArt } from '../../core';
import { colors, highlightTone, mistakeTone, unpaintedTone } from '../theme';

/** Pictures are recorded in "art space", where one cell is UNIT wide; the canvas scales them. */
export const UNIT = 16;

const paints = new Map<string, SkPaint>();
function fill(color: string): SkPaint {
  let paint = paints.get(color);
  if (!paint) {
    paint = Skia.Paint();
    paint.setColor(Skia.Color(color));
    paint.setAntiAlias(false);
    paints.set(color, paint);
  }
  return paint;
}

/** Cell colors. Horizontal runs of one color are merged into a single rect. */
export function recordCells(art: PixelArt, progress: ColoringProgress, selectedColor: number): SkPicture {
  const { width, height } = art;
  const colorOf = (cell: number): string => {
    const target = art.cells[cell];
    const painted = progress.paintedAt(cell);
    const targetRgb = art.palette[target];
    if (painted === target) return toHex(targetRgb);
    if (painted !== UNPAINTED) return toHex(mistakeTone(targetRgb, art.palette[painted]));
    if (target === selectedColor && !progress.isComplete) return toHex(highlightTone(targetRgb));
    return toHex(unpaintedTone(targetRgb));
  };
  return createPicture(
    (canvas) => {
      for (let y = 0; y < height; y++) {
        let runStart = 0;
        let runColor = colorOf(y * width);
        for (let x = 1; x <= width; x++) {
          const color = x < width ? colorOf(y * width + x) : '';
          if (color !== runColor) {
            // +0.5 overlap hides hairline seams between runs when scaled.
            canvas.drawRect(Skia.XYWHRect(runStart * UNIT, y * UNIT, (x - runStart) * UNIT + 0.5, UNIT + 0.5), fill(runColor));
            runStart = x;
            runColor = color;
          }
        }
      }
    },
    { width: width * UNIT, height: height * UNIT },
  );
}

/** Thin grid lines, shown once cells are big enough to tell apart. */
export function recordGrid(art: PixelArt): SkPicture {
  const paint = Skia.Paint();
  paint.setColor(Skia.Color('rgba(31, 36, 48, 0.12)'));
  paint.setStrokeWidth(UNIT / 24);
  return createPicture(
    (canvas) => {
      for (let x = 0; x <= art.width; x++) canvas.drawLine(x * UNIT, 0, x * UNIT, art.height * UNIT, paint);
      for (let y = 0; y <= art.height; y++) canvas.drawLine(0, y * UNIT, art.width * UNIT, y * UNIT, paint);
    },
    { width: art.width * UNIT, height: art.height * UNIT },
  );
}

/** Font size of cell numbers in art space (42% of a cell, as on Android). */
export const NUMBER_FONT_SIZE = UNIT * 0.42;

/** Numbers on every cell that isn't painted correctly; wrong cells get a red number. */
export function recordNumbers(art: PixelArt, progress: ColoringProgress, selectedColor: number, f: SkFont): SkPicture {
  const normal = Skia.Paint();
  normal.setColor(Skia.Color('rgba(31, 36, 48, 0.55)'));
  const selected = Skia.Paint();
  selected.setColor(Skia.Color('rgba(31, 36, 48, 0.9)'));
  const wrong = Skia.Paint();
  wrong.setColor(Skia.Color(colors.warning));
  const widths = new Map<number, number>();
  const baseline = (UNIT + NUMBER_FONT_SIZE * 0.72) / 2;
  return createPicture(
    (canvas) => {
      for (let cell = 0; cell < art.cellCount; cell++) {
        const target = art.cells[cell];
        const painted = progress.paintedAt(cell);
        if (painted === target) continue;
        const label = String(target + 1);
        let w = widths.get(target);
        if (w === undefined) {
          w = f.measureText(label).width;
          widths.set(target, w);
        }
        const x = (cell % art.width) * UNIT + (UNIT - w) / 2;
        const y = Math.floor(cell / art.width) * UNIT + baseline;
        const paint = painted !== UNPAINTED ? wrong : target === selectedColor ? selected : normal;
        canvas.drawText(label, x, y, paint, f);
      }
    },
    { width: art.width * UNIT, height: art.height * UNIT },
  );
}
