import { useEffect, useState, useSyncExternalStore } from 'react';
import { ColoringSession, type ColoringState } from '../../core';
import { repository } from '../../platform/repository';

type Loaded = { status: 'loading' } | { status: 'missing' } | { status: 'ready'; session: ColoringSession };

const noopSubscribe = () => () => {};
const noState = () => null;

/** Loads one artwork and owns its coloring session for the lifetime of the screen. */
export function useColoringSession(artId: string): {
  status: Loaded['status'];
  session: ColoringSession | null;
  state: ColoringState | null;
} {
  const [loaded, setLoaded] = useState<Loaded>({ status: 'loading' });

  useEffect(() => {
    let cancelled = false;
    setLoaded({ status: 'loading' });
    (async () => {
      const art = await repository.art(artId);
      if (cancelled) return;
      if (!art) {
        setLoaded({ status: 'missing' });
        return;
      }
      const progress = await repository.progress(art);
      if (cancelled) return;
      const session = new ColoringSession(art, progress, (p) => void repository.saveProgress(p));
      setLoaded({ status: 'ready', session });
    })();
    return () => {
      cancelled = true;
    };
  }, [artId]);

  const session = loaded.status === 'ready' ? loaded.session : null;
  const state = useSyncExternalStore(session?.subscribe ?? noopSubscribe, session?.getState ?? noState);
  return { status: loaded.status, session, state };
}
