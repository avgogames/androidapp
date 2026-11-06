import { describe, expect, it } from '@jest/globals';
import { ColoringProgress, createPixelArt, Category, UNPAINTED } from '..';

const art = createPixelArt({
  id: 't',
  title: 'Test',
  category: Category.ANIME,
  width: 2,
  height: 2,
  palette: [0x000000, 0xffffff],
  cells: [0, 1, 1, 0],
});

describe('ColoringProgress', () => {
  it('counts correct cells and completes', () => {
    let p = ColoringProgress.empty(art);
    expect(p.fraction).toBe(0);
    expect(p.isStarted).toBe(false);
    p = p.with(art, new Map([[0, 0], [1, 0]]));
    expect(p.correctCount).toBe(1);
    expect(p.isStarted).toBe(true);
    p = p.with(art, new Map([[1, 1], [2, 1], [3, 0]]));
    expect(p.isComplete).toBe(true);
    expect(p.correctCountPerColor(art)).toEqual([2, 2]);
  });

  it('returns the same instance when nothing changes', () => {
    const p = ColoringProgress.empty(art);
    expect(p.with(art, new Map([[0, UNPAINTED]]))).toBe(p);
  });

  it('restores defensively', () => {
    const p = ColoringProgress.restore(art, [0, 9, -1, 0]);
    expect(p.paintedAt(1)).toBe(UNPAINTED);
    expect(p.correctCount).toBe(2);
    expect(ColoringProgress.restore(art, [0]).isStarted).toBe(false);
  });

  it('rejects invalid art', () => {
    expect(() =>
      createPixelArt({ id: 'x', title: 'x', category: Category.ANIME, width: 1, height: 1, palette: [0], cells: [3] }),
    ).toThrow();
  });
});
