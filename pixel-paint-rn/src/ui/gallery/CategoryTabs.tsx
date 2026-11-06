import { Pressable, ScrollView, StyleSheet, Text } from 'react-native';
import { CATEGORY_ORDER, categoryLabel, type Category } from '../../core';
import { colors, shadow, type } from '../theme';

export function CategoryTabs({ selected, onSelect }: { selected: Category; onSelect: (c: Category) => void }) {
  return (
    <ScrollView horizontal showsHorizontalScrollIndicator={false} contentContainerStyle={styles.row}>
      {CATEGORY_ORDER.map((category) => {
        const isSelected = category === selected;
        return (
          <Pressable
            key={category}
            accessibilityRole="tab"
            accessibilityState={{ selected: isSelected }}
            onPress={() => onSelect(category)}
            style={[styles.chip, isSelected ? styles.chipSelected : styles.chipIdle]}
          >
            <Text style={[type.label, { color: isSelected ? '#FFFFFF' : colors.ink }]}>{categoryLabel[category]}</Text>
          </Pressable>
        );
      })}
    </ScrollView>
  );
}

const styles = StyleSheet.create({
  row: { gap: 8, paddingHorizontal: 16 },
  chip: { paddingHorizontal: 16, paddingVertical: 9, borderRadius: 999 },
  chipSelected: { backgroundColor: colors.accent, ...shadow },
  chipIdle: { backgroundColor: colors.surface, borderWidth: 1, borderColor: colors.outline },
});
