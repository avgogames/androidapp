/** On iOS and Android Skia is a native module and ready immediately. */
export function useSkiaReady(): boolean {
  return true;
}
