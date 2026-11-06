import { MaterialCommunityIcons } from '@expo/vector-icons';
import { memo } from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import type { ArtSummary } from '../../core';
import { PixelArtImage } from '../components/PixelArtImage';
import { colors, radius, shadow, type } from '../theme';

export const ArtCard = memo(function ArtCard({
  summary,
  width,
  onPress,
  onLongPress,
}: {
  summary: ArtSummary;
  width: number;
  onPress: (id: string) => void;
  onLongPress?: (summary: ArtSummary) => void;
}) {
  const { art, progress } = summary;
  const percent = Math.round(progress.fraction * 100);
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={`${art.title}, ${progress.isComplete ? 'completed' : `${percent}% painted`}`}
      onPress={() => onPress(art.id)}
      onLongPress={onLongPress ? () => onLongPress(summary) : undefined}
      style={({ pressed }) => [styles.card, { width }, pressed && { transform: [{ scale: 0.98 }] }]}
    >
      <View style={styles.preview}>
        <PixelArtImage art={art} progress={progress} style={StyleSheet.absoluteFill} />
        {progress.isComplete ? (
          <View style={styles.badge}>
            <MaterialCommunityIcons name="check" size={16} color="#FFFFFF" />
          </View>
        ) : null}
      </View>
      <View style={styles.row}>
        <Text style={[type.titleSmall, { flex: 1 }]} numberOfLines={1}>
          {art.title}
        </Text>
        <Text style={type.labelSmall}>{percent}%</Text>
      </View>
      <View style={styles.track}>
        <View
          style={[styles.fill, { width: `${percent}%`, backgroundColor: progress.isComplete ? colors.success : colors.accent }]}
        />
      </View>
    </Pressable>
  );
});

const styles = StyleSheet.create({
  card: { backgroundColor: colors.surface, borderRadius: 16, ...shadow },
  preview: {
    aspectRatio: 1,
    margin: 10,
    borderRadius: radius.sm,
    overflow: 'hidden',
    backgroundColor: colors.cellLight,
  },
  badge: {
    position: 'absolute',
    top: 6,
    right: 6,
    width: 24,
    height: 24,
    borderRadius: 12,
    backgroundColor: colors.success,
    alignItems: 'center',
    justifyContent: 'center',
  },
  row: { flexDirection: 'row', alignItems: 'center', gap: 6, paddingHorizontal: 12, paddingBottom: 4 },
  track: { height: 4, borderRadius: 2, marginHorizontal: 12, marginTop: 2, marginBottom: 12, backgroundColor: colors.surfaceVariant, overflow: 'hidden' },
  fill: { height: 4, borderRadius: 2 },
});
