import type { ColoringProgress } from './coloringProgress';
import type { PixelArt } from './pixelArt';

/** What the gallery shows for one artwork: the art itself plus the saved painting state. */
export interface ArtSummary {
  readonly art: PixelArt;
  readonly progress: ColoringProgress;
}
