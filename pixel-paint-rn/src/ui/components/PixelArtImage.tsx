import { AlphaType, ColorType, FilterMode, MipmapMode, Skia, type SkImage } from '@shopify/react-native-skia';
import { useMemo } from 'react';
import { Image, type ImageStyle, type StyleProp } from 'react-native';
import { blue, green, red, type ColoringProgress, type PixelArt } from '../../core';
import { unpaintedTone } from '../theme';

/** One pixel per cell: finished cells in full color, the rest as paper tones. */
export function renderArtImage(art: PixelArt, progress: ColoringProgress | null, revealAll = false): SkImage | null {
  const bytes = new Uint8Array(art.cellCount * 4);
  for (let cell = 0; cell < art.cellCount; cell++) {
    const target = art.palette[art.cells[cell]];
    const color = revealAll || progress?.isCorrect(art, cell) ? target : unpaintedTone(target);
    bytes[cell * 4] = red(color);
    bytes[cell * 4 + 1] = green(color);
    bytes[cell * 4 + 2] = blue(color);
    bytes[cell * 4 + 3] = 255;
  }
  return Skia.Image.MakeImage(
    { width: art.width, height: art.height, colorType: ColorType.RGBA_8888, alphaType: AlphaType.Unpremul },
    Skia.Data.fromBytes(bytes),
    art.width * 4,
  );
}

/** Thumbnails are pre-scaled with hard pixel edges, so the platform's smooth scaling stays crisp. */
const THUMB_PX = 384;

const byProgress = new WeakMap<ColoringProgress, string>();
const revealed = new WeakMap<PixelArt, string>();
const blank = new WeakMap<PixelArt, string>();

/**
 * A PNG data URI of the artwork, cached per immutable progress snapshot. A gallery card is then
 * a plain <Image> instead of its own GPU canvas, which keeps a grid of 100 cards cheap on every
 * platform (browsers cap the number of WebGL contexts, too).
 */
export function artThumbnailUri(art: PixelArt, progress: ColoringProgress | null, revealAll = false): string | null {
  const cache = revealAll ? revealed : progress ? null : blank;
  const hit = revealAll || !progress ? cache!.get(art) : byProgress.get(progress);
  if (hit) return hit;

  const image = renderArtImage(art, progress, revealAll);
  const k = Math.max(1, Math.floor(THUMB_PX / Math.max(art.width, art.height)));
  const surface = Skia.Surface.Make(art.width * k, art.height * k);
  if (!image || !surface) return null;
  surface
    .getCanvas()
    .drawImageRectOptions(
      image,
      Skia.XYWHRect(0, 0, art.width, art.height),
      Skia.XYWHRect(0, 0, art.width * k, art.height * k),
      FilterMode.Nearest,
      MipmapMode.None,
    );
  surface.flush();
  const uri = `data:image/png;base64,${surface.makeImageSnapshot().encodeToBase64()}`;
  if (revealAll || !progress) cache!.set(art, uri);
  else byProgress.set(progress, uri);
  return uri;
}

/** Crisp preview of an artwork, scaled to fit its bounds. */
export function PixelArtImage({
  art,
  progress,
  revealAll = false,
  style,
}: {
  art: PixelArt;
  progress: ColoringProgress | null;
  revealAll?: boolean;
  style?: StyleProp<ImageStyle>;
}) {
  const uri = useMemo(() => artThumbnailUri(art, progress, revealAll), [art, progress, revealAll]);
  return uri ? <Image source={{ uri }} style={style} resizeMode="contain" fadeDuration={0} accessibilityIgnoresInvertColors /> : null;
}
