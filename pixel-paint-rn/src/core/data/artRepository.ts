import { allArts } from '../catalog/artCatalog';
import type { ArtSummary } from '../model/artSummary';
import { ColoringProgress } from '../model/coloringProgress';
import type { PixelArt } from '../model/pixelArt';
import { artFromJson, artToJson, progressFromString, progressToString } from './codec';
import type { KeyValueStore } from './keyValueStore';

const PREFIX = 'pixelpaint:v1:';
const ART_KEY = `${PREFIX}art:`;
const PROGRESS_KEY = `${PREFIX}progress:`;

/** Immutable snapshot of everything the gallery shows. Replaced, never mutated. */
export interface Library {
  readonly arts: readonly PixelArt[];
  readonly progress: ReadonlyMap<string, ColoringProgress>;
  readonly summaries: readonly ArtSummary[];
}

interface StoredImport {
  createdAt: number;
  art: string;
}

/**
 * Single source of truth for artworks and painting progress. Built-in art comes from the
 * bundled catalog; imports and progress live in the key-value store. Everything is loaded once,
 * and every write goes to memory first (so the UI updates instantly), then to storage through a
 * serial queue so writes never interleave.
 */
export class ArtRepository {
  private library: Library | null = null;
  private loading: Promise<Library> | null = null;
  private writes: Promise<void> = Promise.resolve();
  private readonly listeners = new Set<() => void>();
  private readonly builtInIds: ReadonlySet<string>;

  constructor(
    private readonly store: KeyValueStore,
    private readonly builtIns: readonly PixelArt[] = allArts(),
    private readonly onError: (error: unknown) => void = () => {},
  ) {
    this.builtInIds = new Set(builtIns.map((a) => a.id));
  }

  /** Current snapshot, or null before the first load finishes. */
  getSnapshot = (): Library | null => this.library;

  subscribe = (listener: () => void): (() => void) => {
    this.listeners.add(listener);
    void this.load();
    return () => this.listeners.delete(listener);
  };

  load(): Promise<Library> {
    if (this.library) return Promise.resolve(this.library);
    this.loading ??= this.readLibrary().then((lib) => {
      this.library = lib;
      this.emit();
      return lib;
    });
    return this.loading;
  }

  async art(id: string): Promise<PixelArt | undefined> {
    return (await this.load()).arts.find((a) => a.id === id);
  }

  async progress(art: PixelArt): Promise<ColoringProgress> {
    return (await this.load()).progress.get(art.id) ?? ColoringProgress.empty(art);
  }

  async saveProgress(progress: ColoringProgress): Promise<void> {
    const lib = await this.load();
    const map = new Map(lib.progress).set(progress.artId, progress);
    this.update(lib.arts, map);
    await this.write(() => this.store.setItem(PROGRESS_KEY + progress.artId, progressToString(progress)));
  }

  /** Clears every painted cell of art and returns the blank progress. */
  async resetProgress(art: PixelArt): Promise<ColoringProgress> {
    const lib = await this.load();
    const map = new Map(lib.progress);
    map.delete(art.id);
    this.update(lib.arts, map);
    await this.write(() => this.store.multiRemove([PROGRESS_KEY + art.id]));
    return ColoringProgress.empty(art);
  }

  async saveImported(art: PixelArt): Promise<void> {
    const lib = await this.load();
    const stored: StoredImport = { createdAt: Date.now(), art: artToJson(art) };
    await this.write(() => this.store.setItem(ART_KEY + art.id, JSON.stringify(stored)));
    this.update([art, ...lib.arts.filter((a) => a.id !== art.id)], lib.progress);
  }

  async deleteImported(id: string): Promise<void> {
    if (this.builtInIds.has(id)) return;
    const lib = await this.load();
    await this.write(() => this.store.multiRemove([ART_KEY + id, PROGRESS_KEY + id]));
    const map = new Map(lib.progress);
    map.delete(id);
    this.update(
      lib.arts.filter((a) => a.id !== id),
      map,
    );
  }

  /** Resolves once every queued write has reached storage. */
  flush(): Promise<void> {
    return this.writes;
  }

  private write(op: () => Promise<void>): Promise<void> {
    const next = this.writes.then(op).catch((e) => this.onError(e));
    this.writes = next;
    return next;
  }

  private update(arts: readonly PixelArt[], progress: ReadonlyMap<string, ColoringProgress>): void {
    this.library = makeLibrary(arts, progress);
    this.emit();
  }

  private emit(): void {
    this.listeners.forEach((l) => l());
  }

  private async readLibrary(): Promise<Library> {
    let entries: readonly (readonly [string, string | null])[] = [];
    try {
      const keys = (await this.store.getAllKeys()).filter((k) => k.startsWith(PREFIX));
      entries = keys.length ? await this.store.multiGet(keys) : [];
    } catch (e) {
      this.onError(e);
    }

    const imported: { createdAt: number; art: PixelArt }[] = [];
    const rawProgress: [string, string][] = [];
    for (const [key, value] of entries) {
      if (value == null) continue;
      if (key.startsWith(ART_KEY)) {
        try {
          const stored = JSON.parse(value) as StoredImport;
          imported.push({ createdAt: stored.createdAt, art: artFromJson(stored.art) });
        } catch (e) {
          this.onError(e); // A corrupt import is skipped, never fatal.
        }
      } else if (key.startsWith(PROGRESS_KEY)) {
        rawProgress.push([key.slice(PROGRESS_KEY.length), value]);
      }
    }
    imported.sort((a, b) => b.createdAt - a.createdAt);
    const arts = [...imported.map((i) => i.art).filter((a) => !this.builtInIds.has(a.id)), ...this.builtIns];
    const byId = new Map(arts.map((a) => [a.id, a]));
    const progress = new Map<string, ColoringProgress>();
    for (const [artId, text] of rawProgress) {
      const art = byId.get(artId);
      if (!art) continue;
      try {
        progress.set(artId, progressFromString(art, text));
      } catch (e) {
        this.onError(e);
      }
    }
    return makeLibrary(arts, progress);
  }
}

function makeLibrary(arts: readonly PixelArt[], progress: ReadonlyMap<string, ColoringProgress>): Library {
  return {
    arts,
    progress,
    summaries: arts.map((art) => ({ art, progress: progress.get(art.id) ?? ColoringProgress.empty(art) })),
  };
}
