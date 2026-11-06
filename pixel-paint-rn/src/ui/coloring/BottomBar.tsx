import { MaterialCommunityIcons } from '@expo/vector-icons';
import { memo, useMemo } from 'react';
import { FlatList, Pressable, StyleSheet, Text, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { MAX_BRUSH, MIN_BRUSH, toHex, usesBrushSize, type ColoringState } from '../../core';
import { SquareIconButton } from '../components/SquareIconButton';
import { Stepper } from '../components/Stepper';
import { colors, inkOn, type } from '../theme';
import { toolIcon, toolLabel } from './toolInfo';

export interface BottomBarActions {
  onOpenTools: () => void;
  onSelectColor: (index: number) => void;
  onBrushSize: (size: number) => void;
  onUndo: () => void;
  onRedo: () => void;
}

/** Tool row (tool, selected color, brush size, undo/redo) above the numbered palette strip. */
export function BottomBar({ state, toolsOpen, ...actions }: { state: ColoringState; toolsOpen: boolean } & BottomBarActions) {
  const insets = useSafeAreaInsets();
  const { art, progress, selectedColor, tool, brushSize } = state;
  const done = useMemo(() => progress.correctCountPerColor(art), [art, progress]);
  const swatch = art.palette[selectedColor];

  return (
    <View style={[styles.sheet, { paddingBottom: Math.max(insets.bottom, 8) }]}>
      <View style={styles.toolRow}>
        <SquareIconButton icon={toolIcon[tool]} label={`Tools, current ${toolLabel[tool]}`} active={toolsOpen} onPress={actions.onOpenTools} size={48} />
        <View style={styles.selected} accessibilityLabel={`Selected color ${selectedColor + 1}`}>
          <View style={[styles.selectedDot, { backgroundColor: toHex(swatch) }]}>
            <Text style={[styles.selectedNumber, { color: inkOn(swatch) }]}>{selectedColor + 1}</Text>
          </View>
        </View>
        {usesBrushSize(tool) ? (
          <Stepper label="Brush size" value={brushSize} min={MIN_BRUSH} max={MAX_BRUSH} onChange={actions.onBrushSize} />
        ) : null}
        <View style={{ flex: 1 }} />
        <SquareIconButton icon="undo-variant" label="Undo" disabled={!state.canUndo} onPress={actions.onUndo} />
        <SquareIconButton icon="redo-variant" label="Redo" disabled={!state.canRedo} onPress={actions.onRedo} />
      </View>
      <FlatList
        horizontal
        data={art.palette as number[]}
        keyExtractor={(_, i) => String(i)}
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.palette}
        renderItem={({ item, index }) => (
          <PaletteSwatch
            color={item}
            index={index}
            selected={index === selectedColor}
            fraction={done[index] / art.cellCountPerColor[index]}
            onPress={actions.onSelectColor}
          />
        )}
        extraData={`${selectedColor}:${progress.correctCount}`}
      />
    </View>
  );
}

const PaletteSwatch = memo(function PaletteSwatch({
  color,
  index,
  selected,
  fraction,
  onPress,
}: {
  color: number;
  index: number;
  selected: boolean;
  fraction: number;
  onPress: (index: number) => void;
}) {
  const ink = inkOn(color);
  const finished = fraction >= 1;
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={finished ? `Color ${index + 1} finished` : `Color ${index + 1}`}
      accessibilityState={{ selected }}
      onPress={() => onPress(index)}
      style={[styles.swatch, { backgroundColor: toHex(color), height: selected ? 52 : 44 }, selected && styles.swatchSelected]}
    >
      {finished ? (
        <MaterialCommunityIcons name="check" size={18} color={ink} style={{ opacity: 0.8 }} />
      ) : (
        <Text style={[styles.swatchNumber, { color: ink }]}>{index + 1}</Text>
      )}
      {!finished ? (
        <View style={styles.swatchTrack}>
          <View style={[styles.swatchFill, { width: `${Math.round(fraction * 100)}%`, backgroundColor: ink }]} />
        </View>
      ) : null}
    </Pressable>
  );
});

const styles = StyleSheet.create({
  sheet: {
    backgroundColor: colors.surface,
    borderTopLeftRadius: 22,
    borderTopRightRadius: 22,
    paddingTop: 12,
    shadowColor: '#1F2430',
    shadowOpacity: 0.12,
    shadowRadius: 16,
    shadowOffset: { width: 0, height: -4 },
    elevation: 12,
  },
  toolRow: { flexDirection: 'row', alignItems: 'center', gap: 8, paddingHorizontal: 12 },
  selected: {
    width: 48,
    height: 48,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: colors.outline,
    alignItems: 'center',
    justifyContent: 'center',
  },
  selectedDot: {
    width: 32,
    height: 32,
    borderRadius: 16,
    borderWidth: 1,
    borderColor: colors.outline,
    alignItems: 'center',
    justifyContent: 'center',
  },
  selectedNumber: { fontSize: 13, fontWeight: '700' },
  palette: { paddingHorizontal: 12, paddingTop: 12, paddingBottom: 4, gap: 6, alignItems: 'flex-end' },
  swatch: {
    width: 44,
    borderRadius: 10,
    borderWidth: 1,
    borderColor: colors.outline,
    alignItems: 'center',
    justifyContent: 'center',
    overflow: 'hidden',
  },
  swatchSelected: { borderWidth: 3, borderColor: colors.accent, elevation: 4 },
  swatchNumber: { ...type.label, fontVariant: ['tabular-nums'] },
  swatchTrack: { position: 'absolute', left: 4, right: 4, bottom: 4, height: 3, borderRadius: 2, backgroundColor: 'rgba(127,127,127,0.25)' },
  swatchFill: { height: 3, borderRadius: 2, opacity: 0.8 },
});
