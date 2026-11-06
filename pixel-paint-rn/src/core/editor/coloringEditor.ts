import { ColoringProgress, UNPAINTED } from '../model/coloringProgress';
import type { PixelArt } from '../model/pixelArt';
import { Tool } from './tool';

/**
 * Pure editing rules for one artwork: which cells a tool touches. Correctly painted cells are
 * never overwritten by painting tools, so a stray stroke cannot undo finished work; the eraser
 * is the explicit way to clear them.
 */
export class ColoringEditor {
  constructor(private readonly art: PixelArt) {}

  changesFor(tool: Tool, cell: number, color: number, brushSize: number, progress: ColoringProgress): Map<number, number> {
    const art = this.art;
    const changes = new Map<number, number>();
    switch (tool) {
      case Tool.PENCIL:
        if (!progress.isCorrect(art, cell)) changes.set(cell, color);
        break;
      case Tool.BRUSH:
        for (const c of this.square(cell, brushSize)) {
          if (art.cells[c] === color && !progress.isCorrect(art, c)) changes.set(c, color);
        }
        break;
      case Tool.ERASER:
        for (const c of this.square(cell, brushSize)) {
          if (progress.paintedAt(c) !== UNPAINTED) changes.set(c, UNPAINTED);
        }
        break;
      case Tool.BUCKET:
        for (const c of this.floodRegion(cell)) {
          if (!progress.isCorrect(art, c)) changes.set(c, color);
        }
        break;
      case Tool.PICKER:
      case Tool.HAND:
        break;
    }
    return changes;
  }

  /** Cells in a size x size square centred on cell, clipped to the artwork. */
  square(cell: number, size: number): number[] {
    const { width, height } = this.art;
    const cx = cell % width;
    const cy = Math.floor(cell / width);
    const start = -Math.trunc((size - 1) / 2);
    const result: number[] = [];
    for (let dy = start; dy < start + size; dy++) {
      for (let dx = start; dx < start + size; dx++) {
        const x = cx + dx;
        const y = cy + dy;
        if (x >= 0 && x < width && y >= 0 && y < height) result.push(y * width + x);
      }
    }
    return result;
  }

  /** 4-connected cells sharing cell's target number. */
  floodRegion(cell: number): number[] {
    const { width, height, cells } = this.art;
    const target = cells[cell];
    const seen = new Uint8Array(cells.length);
    const stack = [cell];
    const region: number[] = [];
    seen[cell] = 1;
    while (stack.length > 0) {
      const current = stack.pop()!;
      region.push(current);
      const x = current % width;
      const y = Math.floor(current / width);
      const visit = (nx: number, ny: number) => {
        if (nx < 0 || nx >= width || ny < 0 || ny >= height) return;
        const next = ny * width + nx;
        if (!seen[next] && cells[next] === target) {
          seen[next] = 1;
          stack.push(next);
        }
      };
      visit(x - 1, y);
      visit(x + 1, y);
      visit(x, y - 1);
      visit(x, y + 1);
    }
    return region;
  }

  /** Cells crossed moving from one cell to another (Bresenham), so fast drags leave no gaps. */
  cellsBetween(from: number, to: number): number[] {
    const width = this.art.width;
    let x0 = from % width;
    let y0 = Math.floor(from / width);
    const x1 = to % width;
    const y1 = Math.floor(to / width);
    const dx = Math.abs(x1 - x0);
    const dy = -Math.abs(y1 - y0);
    const sx = x0 < x1 ? 1 : -1;
    const sy = y0 < y1 ? 1 : -1;
    let err = dx + dy;
    const result: number[] = [];
    for (;;) {
      result.push(y0 * width + x0);
      if (x0 === x1 && y0 === y1) break;
      const e2 = 2 * err;
      if (e2 >= dy) {
        err += dy;
        x0 += sx;
      }
      if (e2 <= dx) {
        err += dx;
        y0 += sy;
      }
    }
    return result;
  }
}
