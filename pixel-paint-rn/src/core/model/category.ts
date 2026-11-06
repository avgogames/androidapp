/** Gallery sections. IMPORTED holds artworks the player converted from their own photos. */
export const Category = {
  ANIME: 'ANIME',
  CARS: 'CARS',
  ANIMALS: 'ANIMALS',
  SCENES: 'SCENES',
  IMPORTED: 'IMPORTED',
} as const;

export type Category = (typeof Category)[keyof typeof Category];

export const CATEGORY_ORDER: readonly Category[] = [
  Category.ANIME,
  Category.CARS,
  Category.ANIMALS,
  Category.SCENES,
  Category.IMPORTED,
];

export const categoryLabel: Record<Category, string> = {
  ANIME: 'Anime',
  CARS: 'Cars',
  ANIMALS: 'Animals',
  SCENES: 'Scenes',
  IMPORTED: 'My imports',
};

export function isCategory(value: unknown): value is Category {
  return typeof value === 'string' && value in categoryLabel;
}
