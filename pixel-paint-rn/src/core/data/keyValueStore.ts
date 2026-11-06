/** The minimal storage the repository needs. AsyncStorage implements it on device. */
export interface KeyValueStore {
  getAllKeys(): Promise<readonly string[]>;
  multiGet(keys: readonly string[]): Promise<readonly (readonly [string, string | null])[]>;
  setItem(key: string, value: string): Promise<void>;
  multiRemove(keys: readonly string[]): Promise<void>;
}

/** In-memory store for tests and previews. */
export class MemoryStore implements KeyValueStore {
  readonly data = new Map<string, string>();

  async getAllKeys() {
    return [...this.data.keys()];
  }

  async multiGet(keys: readonly string[]) {
    return keys.map((k) => [k, this.data.get(k) ?? null] as const);
  }

  async setItem(key: string, value: string) {
    this.data.set(key, value);
  }

  async multiRemove(keys: readonly string[]) {
    keys.forEach((k) => this.data.delete(k));
  }
}
