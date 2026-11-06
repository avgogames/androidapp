import type { PixelArt } from '../../core';

export interface Point {
  x: number;
  y: number;
}

/** Zoom and pan of the canvas. Pure view mechanics, so it never reaches the coloring session. */
export interface Viewport {
  scale: number;
  offset: Point;
}

export interface Size {
  width: number;
  height: number;
}

export const FIT_VIEWPORT: Viewport = { scale: 1, offset: { x: 0, y: 0 } };

const FIT_MARGIN = 0.94;
const MIN_MAX_SCALE = 3;

/** Cell size and top-left of the artwork for a canvas of the given size. */
export function gridLayout(viewport: Viewport, size: Size, art: PixelArt): { cell: number; origin: Point } {
  const base = Math.min(size.width / art.width, size.height / art.height) * FIT_MARGIN;
  const cell = base * viewport.scale;
  return {
    cell,
    origin: {
      x: (size.width - cell * art.width) / 2 + viewport.offset.x,
      y: (size.height - cell * art.height) / 2 + viewport.offset.y,
    },
  };
}

/** Row-major cell index under a point, or null outside the artwork. */
export function cellAt(viewport: Viewport, size: Size, art: PixelArt, point: Point): number | null {
  const { cell, origin } = gridLayout(viewport, size, art);
  const x = Math.floor((point.x - origin.x) / cell);
  const y = Math.floor((point.y - origin.y) / cell);
  return x >= 0 && x < art.width && y >= 0 && y < art.height ? y * art.width + x : null;
}

/**
 * Applies a pinch/pan step. The point under the fingers stays fixed while zooming, zoom is
 * clamped between "fit" and a size where cells are comfortably tappable, and the artwork can't
 * be dragged far off screen.
 */
export function transformViewport(
  viewport: Viewport,
  size: Size,
  art: PixelArt,
  centroid: Point,
  zoom: number,
  pan: Point,
  comfortableCell: number,
): Viewport {
  const before = gridLayout(viewport, size, art);
  const base = before.cell / viewport.scale;
  const maxScale = Math.max(MIN_MAX_SCALE, (comfortableCell * 1.6) / base);
  const scale = Math.min(maxScale, Math.max(1, viewport.scale * zoom));
  const ratio = scale / viewport.scale;
  const newOrigin = {
    x: centroid.x - (centroid.x - before.origin.x) * ratio + pan.x,
    y: centroid.y - (centroid.y - before.origin.y) * ratio + pan.y,
  };
  const cell = base * scale;
  const centred = { x: (size.width - cell * art.width) / 2, y: (size.height - cell * art.height) / 2 };
  const limitX = Math.max(0, (cell * art.width - size.width) / 2) + size.width * 0.35;
  const limitY = Math.max(0, (cell * art.height - size.height) / 2) + size.height * 0.35;
  return {
    scale,
    offset: {
      x: Math.min(limitX, Math.max(-limitX, newOrigin.x - centred.x)),
      y: Math.min(limitY, Math.max(-limitY, newOrigin.y - centred.y)),
    },
  };
}
