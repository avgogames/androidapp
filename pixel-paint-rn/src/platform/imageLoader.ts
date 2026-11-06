import { AlphaType, ColorType, Skia } from '@shopify/react-native-skia';
import { ImageManipulator, SaveFormat } from 'expo-image-manipulator';
import * as ImagePicker from 'expo-image-picker';
import type { DecodedImage } from '../core';

/** Photos are decoded at most this big, so a 12 MP photo never needs full-size memory. */
const MAX_DECODE_PX = 512;

/**
 * Lets the player pick a photo and returns its pixels, or null if they cancelled. The image is
 * resized natively first (which also applies EXIF rotation), then decoded by Skia into
 * non-premultiplied RGBA for the converter.
 */
export async function pickAndDecodeImage(): Promise<DecodedImage | null> {
  const result = await ImagePicker.launchImageLibraryAsync({ mediaTypes: ['images'], quality: 1 });
  const asset = result.canceled ? undefined : result.assets[0];
  if (!asset) return null;

  const context = ImageManipulator.manipulate(asset.uri);
  if (asset.width > MAX_DECODE_PX || asset.height > MAX_DECODE_PX) {
    context.resize(asset.width >= asset.height ? { width: MAX_DECODE_PX } : { height: MAX_DECODE_PX });
  }
  const rendered = await context.renderAsync();
  const saved = await rendered.saveAsync({ format: SaveFormat.PNG, base64: true });
  if (!saved.base64) throw new Error('Image could not be encoded');

  const image = Skia.Image.MakeImageFromEncoded(Skia.Data.fromBase64(saved.base64));
  if (!image) throw new Error('Image could not be decoded');
  const width = image.width();
  const height = image.height();
  const rgba = image.readPixels(0, 0, {
    width,
    height,
    colorType: ColorType.RGBA_8888,
    alphaType: AlphaType.Unpremul,
  });
  if (!rgba || rgba.length !== width * height * 4) throw new Error('Image pixels could not be read');
  return { width, height, rgba };
}
