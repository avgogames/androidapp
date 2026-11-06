import { describe, expect, it } from '@jest/globals';
import { allArts, artsInCategory, Category, findBuiltIn } from '..';

describe('bundled catalog', () => {
  const arts = allArts();

  it('has 100 artworks, 25 per category', () => {
    expect(arts).toHaveLength(100);
    for (const c of [Category.ANIME, Category.CARS, Category.ANIMALS, Category.SCENES]) {
      expect(artsInCategory(c)).toHaveLength(25);
    }
  });

  it('has unique ids and titles', () => {
    expect(new Set(arts.map((a) => a.id)).size).toBe(100);
    expect(new Set(arts.map((a) => a.title)).size).toBe(100);
  });

  it('keeps the Android ids so progress formats line up', () => {
    expect(findBuiltIn('anime_01')?.title).toBe('Sakura Student');
    expect(findBuiltIn('scenes_25')).toBeDefined();
  });

  it('is playable: 32x32, 4..24 colors, every color used', () => {
    for (const art of arts) {
      expect(art.width).toBe(32);
      expect(art.height).toBe(32);
      expect(art.palette.length).toBeGreaterThanOrEqual(4);
      expect(art.palette.length).toBeLessThanOrEqual(24);
      expect(art.cellCountPerColor.every((n) => n > 0)).toBe(true);
    }
  });

  it('has visually distinct artworks', () => {
    const prints = arts.map((a) => Array.from(a.cells, (i) => a.palette[i]).join(','));
    expect(new Set(prints).size).toBe(100);
  });
});
