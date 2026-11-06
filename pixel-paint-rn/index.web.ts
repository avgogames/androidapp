// Web entry. On the web, Skia renders through CanvasKit (WebAssembly), and the `Skia` object is
// created when the package is first imported, so CanvasKit must finish loading before any app
// module is evaluated. `npm run web` copies canvaskit.wasm into public/.
import { LoadSkiaWeb } from '@shopify/react-native-skia/lib/module/web';

LoadSkiaWeb({ locateFile: (file: string) => `/${file}` }).then(() => {
  // A synchronous require (not import()) keeps the app in the main bundle.
  require('expo-router/entry');
});
