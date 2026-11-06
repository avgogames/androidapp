import { useSyncExternalStore } from 'react';
import { allArts, ArtRepository, type Library } from '../core';
import { asyncStorageStore } from './storage';

/** The app's one repository. Manual DI: screens import it directly, tests build their own. */
export const repository = new ArtRepository(asyncStorageStore, allArts(), (error) => {
  console.warn('[pixel-paint] storage error', error);
});

/** The current library, or null while the first load is running. */
export function useLibrary(): Library | null {
  return useSyncExternalStore(repository.subscribe, repository.getSnapshot);
}
