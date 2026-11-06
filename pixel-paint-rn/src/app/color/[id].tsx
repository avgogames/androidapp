import { router, useLocalSearchParams } from 'expo-router';
import { useState } from 'react';
import { ActivityIndicator, Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { confirmDestructive } from '../../platform/confirm';
import { repository } from '../../platform/repository';
import { SquareIconButton } from '../../ui/components/SquareIconButton';
import { BottomBar } from '../../ui/coloring/BottomBar';
import { CompletionDialog } from '../../ui/coloring/CompletionDialog';
import { PixelGrid } from '../../ui/coloring/PixelGrid';
import { ToolDrawer } from '../../ui/coloring/ToolDrawer';
import { useColoringSession } from '../../ui/coloring/useColoringSession';
import { colors, type } from '../../ui/theme';

export default function ColoringScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const insets = useSafeAreaInsets();
  const { status, session, state } = useColoringSession(String(id));
  const [toolsOpen, setToolsOpen] = useState(false);
  const [fitKey, setFitKey] = useState(0);

  const back = () => (router.canGoBack() ? router.back() : router.replace('/'));

  if (status !== 'ready' || !session || !state) {
    return (
      <View style={[styles.center, { paddingTop: insets.top }]}>
        {status === 'missing' ? (
          <>
            <Text style={type.body}>This artwork is no longer available.</Text>
            <Pressable onPress={back} accessibilityRole="button">
              <Text style={[type.label, { color: colors.accent }]}>Back to gallery</Text>
            </Pressable>
          </>
        ) : (
          <ActivityIndicator color={colors.accent} />
        )}
      </View>
    );
  }

  const { art, progress } = state;
  const confirmReset = () =>
    confirmDestructive('Reset this artwork?', 'All painted cells will be cleared. This cannot be undone.', 'Reset', () => {
      session.reset();
      void repository.resetProgress(art);
    });

  return (
    <View style={styles.screen}>
      <View style={[styles.topBar, { paddingTop: insets.top + 8 }]}>
        <SquareIconButton icon="arrow-left" label="Back" onPress={back} />
        <View style={styles.titleBlock}>
          <Text style={type.title} numberOfLines={1}>
            {art.title}
          </Text>
          <Text style={[type.labelSmall, { fontVariant: ['tabular-nums'] }]}>
            {Math.round(progress.fraction * 100)}% · {progress.correctCount}/{progress.totalCount}
          </Text>
        </View>
        <SquareIconButton icon="fit-to-screen-outline" label="Fit to screen" onPress={() => setFitKey((k) => k + 1)} />
        <SquareIconButton icon="restore" label="Reset coloring" onPress={confirmReset} disabled={!progress.isStarted} />
      </View>

      <View style={styles.canvas}>
        <PixelGrid
          art={art}
          progress={progress}
          selectedColor={state.selectedColor}
          tool={state.tool}
          fitKey={fitKey}
          onTap={(cell) => session.tap(cell)}
          onStrokeStart={() => session.strokeStart()}
          onStrokeMove={(cell) => session.strokeMove(cell)}
          onStrokeEnd={() => session.strokeEnd()}
        />
        {toolsOpen ? (
          <>
            <Pressable style={StyleSheet.absoluteFill} onPress={() => setToolsOpen(false)} accessibilityLabel="Close tools" />
            <ToolDrawer tool={state.tool} onSelect={(t) => session.selectTool(t)} onClose={() => setToolsOpen(false)} />
          </>
        ) : null}
      </View>

      <BottomBar
        state={state}
        toolsOpen={toolsOpen}
        onOpenTools={() => setToolsOpen((open) => !open)}
        onSelectColor={(i) => session.selectColor(i)}
        onBrushSize={(s) => session.setBrushSize(s)}
        onUndo={() => session.undo()}
        onRedo={() => session.redo()}
      />

      <CompletionDialog
        art={art}
        visible={state.showCompletion}
        onStay={() => session.dismissCompletion()}
        onGallery={() => {
          session.dismissCompletion();
          back();
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.paper },
  center: { flex: 1, alignItems: 'center', justifyContent: 'center', gap: 16, backgroundColor: colors.paper },
  topBar: { flexDirection: 'row', alignItems: 'center', gap: 10, paddingHorizontal: 12, paddingBottom: 8 },
  titleBlock: { flex: 1, gap: 2 },
  canvas: { flex: 1 },
});
