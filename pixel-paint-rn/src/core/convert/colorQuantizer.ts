import { blue, green, red, rgb, type Rgb } from '../color';

/**
 * "Redmean" weighted RGB distance: cheap, and far closer to perceived difference than plain
 * Euclidean RGB, especially for skin tones and greens.
 */
export function colorDistance(a: Rgb, b: Rgb): number {
  const rMean = (red(a) + red(b)) >> 1;
  const dr = red(a) - red(b);
  const dg = green(a) - green(b);
  const db = blue(a) - blue(b);
  return Math.floor(((512 + rMean) * dr * dr) / 256) + 4 * dg * dg + Math.floor(((767 - rMean) * db * db) / 256);
}

/** Index of the palette color perceptually closest to color. */
export function nearestColor(color: Rgb, palette: readonly Rgb[]): number {
  let best = 0;
  let bestDistance = Infinity;
  for (let i = 0; i < palette.length; i++) {
    const d = colorDistance(color, palette[i]);
    if (d < bestDistance) {
      bestDistance = d;
      best = i;
    }
  }
  return best;
}

/**
 * Reduces an image to a small palette: median cut picks well-spread starting colors, then a few
 * k-means passes pull each color to the centre of the pixels it represents. Deterministic for
 * the same input.
 */
export function quantize(pixels: ArrayLike<Rgb>, maxColors: number, iterations = 6): Rgb[] {
  if (maxColors < 1) throw new Error('maxColors must be >= 1');
  const distinct = unique(Array.from(pixels));
  if (distinct.length <= maxColors) return distinct;
  let palette = medianCut(Array.from(pixels), maxColors);
  for (let i = 0; i < iterations; i++) palette = refine(pixels, palette);
  return unique(palette);
}

function unique(values: Rgb[]): Rgb[] {
  return [...new Set(values)];
}

const channelOf = (c: Rgb, channel: number): number => (channel === 0 ? red(c) : channel === 1 ? green(c) : blue(c));

function medianCut(pixels: Rgb[], count: number): Rgb[] {
  const boxes: Rgb[][] = [pixels];
  while (boxes.length < count) {
    // Split the box with the widest single channel range.
    let widest = -1;
    let widestRange = 0;
    let widestChannel = 0;
    boxes.forEach((box, index) => {
      if (box.length < 2) return;
      for (let channel = 0; channel <= 2; channel++) {
        let min = 255;
        let max = 0;
        for (const c of box) {
          const v = channelOf(c, channel);
          if (v < min) min = v;
          if (v > max) max = v;
        }
        if (max - min > widestRange) {
          widestRange = max - min;
          widest = index;
          widestChannel = channel;
        }
      }
    });
    if (widest < 0) break;
    const [box] = boxes.splice(widest, 1);
    // Stable sort, matching Kotlin's sortedBy.
    const sorted = box
      .map((c, i) => [c, i] as const)
      .sort((a, b) => channelOf(a[0], widestChannel) - channelOf(b[0], widestChannel) || a[1] - b[1])
      .map(([c]) => c);
    const mid = sorted.length >> 1;
    boxes.push(sorted.slice(0, mid), sorted.slice(mid));
  }
  return boxes.filter((b) => b.length > 0).map(average);
}

function refine(pixels: ArrayLike<Rgb>, palette: Rgb[]): Rgb[] {
  const n = palette.length;
  const r = new Float64Array(n);
  const g = new Float64Array(n);
  const b = new Float64Array(n);
  const count = new Int32Array(n);
  for (let p = 0; p < pixels.length; p++) {
    const c = pixels[p];
    const i = nearestColor(c, palette);
    r[i] += red(c);
    g[i] += green(c);
    b[i] += blue(c);
    count[i]++;
  }
  return palette.map((color, i) =>
    count[i] === 0 ? color : rgb(Math.floor(r[i] / count[i]), Math.floor(g[i] / count[i]), Math.floor(b[i] / count[i])),
  );
}

function average(colors: Rgb[]): Rgb {
  let r = 0;
  let g = 0;
  let b = 0;
  for (const c of colors) {
    r += red(c);
    g += green(c);
    b += blue(c);
  }
  const n = colors.length;
  return rgb(Math.floor(r / n), Math.floor(g / n), Math.floor(b / n));
}
