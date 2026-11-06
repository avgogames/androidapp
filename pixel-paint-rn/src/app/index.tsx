import { MaterialCommunityIcons } from '@expo/vector-icons';
import { router } from 'expo-router';
import { useCallback, useMemo, useState } from 'react';
import { ActivityIndicator, FlatList, Pressable, StyleSheet, Text, useWindowDimensions, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { Category, type ArtSummary } from '../core';
import { confirmDestructive } from '../platform/confirm';
import { repository, useLibrary } from '../platform/repository';
import { ArtCard } from '../ui/gallery/ArtCard';
import { CategoryTabs } from '../ui/gallery/CategoryTabs';
import { colors, radius, shadow, type } from '../ui/theme';

const GUTTER = 16;
const GAP = 12;
const MIN_CARD = 150;

export default function GalleryScreen() {
  const library = useLibrary();
  const insets = useSafeAreaInsets();
  const { width } = useWindowDimensions();
  const [category, setCategory] = useState<Category>(Category.ANIME);

  const columns = Math.max(2, Math.floor((width - GUTTER * 2 + GAP) / (MIN_CARD + GAP)));
  const cardWidth = (width - GUTTER * 2 - GAP * (columns - 1)) / columns;

  const items = useMemo(() => library?.summaries.filter((s) => s.art.category === category) ?? [], [library, category]);
  const builtIns = library?.summaries.filter((s) => s.art.category !== Category.IMPORTED) ?? [];
  const completed = builtIns.filter((s) => s.progress.isComplete).length;

  const open = useCallback((id: string) => router.push({ pathname: '/color/[id]', params: { id } }), []);
  const confirmDelete = useCallback((summary: ArtSummary) => {
    confirmDestructive(`Delete “${summary.art.title}”?`, 'The artwork and its coloring progress will be removed.', 'Delete', () => {
      void repository.deleteImported(summary.art.id);
    });
  }, []);
  const goImport = () => router.push('/import');

  const header = (
    <View style={{ paddingTop: insets.top + 20, gap: 16, marginHorizontal: -GUTTER }}>
      <View style={{ paddingHorizontal: GUTTER, gap: 4 }}>
        <Text style={type.headline} accessibilityRole="header">
          Pixel Paint
        </Text>
        <Text style={type.body}>
          {completed} of {builtIns.length || 100} artworks finished
        </Text>
      </View>
      <CategoryTabs selected={category} onSelect={setCategory} />
      <View style={{ height: 4 }} />
    </View>
  );

  return (
    <View style={styles.screen}>
      <FlatList
        key={columns}
        data={items}
        numColumns={columns}
        keyExtractor={(s) => s.art.id}
        ListHeaderComponent={header}
        columnWrapperStyle={columns > 1 ? { gap: GAP } : undefined}
        contentContainerStyle={{ paddingHorizontal: GUTTER, gap: GAP, paddingBottom: insets.bottom + 104 }}
        renderItem={({ item }) => (
          <ArtCard
            summary={item}
            width={cardWidth}
            onPress={open}
            onLongPress={item.art.category === Category.IMPORTED ? confirmDelete : undefined}
          />
        )}
        ListEmptyComponent={
          !library ? (
            <ActivityIndicator style={{ padding: 48 }} color={colors.accent} />
          ) : (
            <Pressable onPress={goImport} style={styles.empty} accessibilityRole="button">
              <MaterialCommunityIcons name="image-plus" size={48} color={colors.accent} />
              <Text style={[type.body, { textAlign: 'center' }]}>Turn any photo into a pixel art puzzle.</Text>
            </Pressable>
          )
        }
        initialNumToRender={8}
        windowSize={5}
      />
      <Pressable
        accessibilityRole="button"
        onPress={goImport}
        style={({ pressed }) => [styles.fab, { bottom: insets.bottom + 20 }, pressed && { backgroundColor: colors.accentDark }]}
      >
        <MaterialCommunityIcons name="image-plus" size={22} color="#FFFFFF" />
        <Text style={[type.label, { color: '#FFFFFF' }]}>Import photo</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.paper },
  empty: { backgroundColor: colors.surface, borderRadius: radius.lg, padding: 32, alignItems: 'center', gap: 16 },
  fab: {
    position: 'absolute',
    right: 20,
    height: 56,
    paddingHorizontal: 20,
    borderRadius: 16,
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    backgroundColor: colors.accent,
    ...shadow,
    shadowOpacity: 0.2,
    elevation: 6,
  },
});
