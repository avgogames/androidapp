import { describe, expect, it } from '@jest/globals';
import { allArts, ArtRepository, ColoringProgress, convertToPixelArt, MemoryStore } from '..';

const flag = () =>
  convertToPixelArt('import_1', 'Flag', { width: 2, height: 1, rgba: [255, 0, 0, 255, 0, 0, 255, 255] }, { gridWidth: 16, maxColors: 4 });

describe('ArtRepository', () => {
  it('loads built-ins, then persists progress across instances', async () => {
    const store = new MemoryStore();
    const repo = new ArtRepository(store);
    const art = allArts()[0];
    expect((await repo.load()).summaries).toHaveLength(100);
    const painted = ColoringProgress.empty(art).with(art, new Map([[0, art.cells[0]]]));
    await repo.saveProgress(painted);

    const reopened = new ArtRepository(store);
    expect((await reopened.progress(art)).equals(painted)).toBe(true);
  });

  it('resets progress', async () => {
    const store = new MemoryStore();
    const repo = new ArtRepository(store);
    const art = allArts()[5];
    await repo.saveProgress(ColoringProgress.empty(art).with(art, new Map([[3, 0]])));
    await repo.resetProgress(art);
    expect((await new ArtRepository(store).progress(art)).isStarted).toBe(false);
    expect(store.data.size).toBe(0);
  });

  it('imports and deletes artworks, newest first', async () => {
    const store = new MemoryStore();
    const repo = new ArtRepository(store);
    await repo.saveImported(flag());
    let lib = await new ArtRepository(store).load();
    expect(lib.arts[0].id).toBe('import_1');
    expect(lib.arts[0].palette).toEqual([0x0000ff, 0xff0000]);
    await repo.deleteImported('import_1');
    lib = await new ArtRepository(store).load();
    expect(lib.arts).toHaveLength(100);
  });

  it('never deletes built-ins and skips corrupt data', async () => {
    const store = new MemoryStore();
    store.data.set('pixelpaint:v1:art:bad', '{not json');
    store.data.set('pixelpaint:v1:progress:anime_01', 'garbage');
    const errors: unknown[] = [];
    const repo = new ArtRepository(store, allArts(), (e) => errors.push(e));
    await repo.deleteImported('anime_01');
    const lib = await repo.load();
    expect(lib.arts).toHaveLength(100);
    expect(lib.progress.get('anime_01')?.isStarted ?? false).toBe(false);
    expect(errors.length).toBeGreaterThan(0);
  });

  it('notifies subscribers', async () => {
    const repo = new ArtRepository(new MemoryStore());
    const calls: number[] = [];
    repo.subscribe(() => calls.push(1));
    await repo.load();
    await repo.saveImported(flag());
    expect(calls.length).toBeGreaterThanOrEqual(2);
  });
});
