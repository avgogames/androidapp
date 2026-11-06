import { artFromStored } from '../data/codec';
import type { Category } from '../model/category';
import type { PixelArt } from '../model/pixelArt';
import catalog from './catalog.json';

/**
 * The 100 bundled artworks (25 per category). The data is exported from the Android app's
 * Kotlin catalog (see scripts/export-catalog), so both apps ship identical pixels and ids, and
 * saved progress stays valid across updates.
 */
let cached: readonly PixelArt[] | null = null;

export function allArts(): readonly PixelArt[] {
  cached ??= Object.freeze(catalog.arts.map(artFromStored));
  return cached;
}

export function artsInCategory(category: Category): PixelArt[] {
  return allArts().filter((a) => a.category === category);
}

export function findBuiltIn(id: string): PixelArt | undefined {
  return allArts().find((a) => a.id === id);
}
