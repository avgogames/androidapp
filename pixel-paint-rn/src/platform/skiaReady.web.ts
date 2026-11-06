import { LoadSkiaWeb } from '@shopify/react-native-skia/lib/module/web';
import { useEffect, useState } from 'react';

// The web preview renders Skia through CanvasKit (WebAssembly), which must finish loading
// before any Skia API is used. `npm run web` copies canvaskit.wasm into public/.
let ready = false;
const loading = LoadSkiaWeb({ locateFile: (file: string) => `/${file}` }).then(() => {
  ready = true;
});

export function useSkiaReady(): boolean {
  const [isReady, setReady] = useState(ready);
  useEffect(() => {
    if (!isReady) void loading.then(() => setReady(true));
  }, [isReady]);
  return isReady;
}
