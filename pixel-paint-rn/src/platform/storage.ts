import AsyncStorage from '@react-native-async-storage/async-storage';
import type { KeyValueStore } from '../core';

/** AsyncStorage behind the repository's storage interface. */
export const asyncStorageStore: KeyValueStore = {
  getAllKeys: () => AsyncStorage.getAllKeys(),
  multiGet: (keys) => AsyncStorage.multiGet(keys),
  setItem: (key, value) => AsyncStorage.setItem(key, value),
  multiRemove: (keys) => AsyncStorage.multiRemove(keys),
};
