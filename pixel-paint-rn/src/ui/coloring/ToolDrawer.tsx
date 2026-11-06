import { MaterialCommunityIcons } from '@expo/vector-icons';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { TOOLS, type Tool } from '../../core';
import { colors, type } from '../theme';
import { toolIcon, toolLabel } from './toolInfo';

/** The tool list that slides over the canvas from the left, like the reference editor. */
export function ToolDrawer({ tool, onSelect, onClose }: { tool: Tool; onSelect: (tool: Tool) => void; onClose: () => void }) {
  return (
    <View style={styles.drawer} accessibilityViewIsModal>
      <Text style={styles.heading}>TOOLS</Text>
      {TOOLS.map((t) => {
        const selected = t === tool;
        return (
          <Pressable
            key={t}
            accessibilityRole="button"
            accessibilityState={{ selected }}
            onPress={() => {
              onSelect(t);
              onClose();
            }}
            style={({ pressed }) => [styles.item, selected && styles.itemSelected, pressed && !selected && styles.itemPressed]}
          >
            <MaterialCommunityIcons name={toolIcon[t]} size={22} color={selected ? '#FFFFFF' : colors.ink} />
            <Text style={[styles.itemLabel, selected && { color: '#FFFFFF' }]}>{toolLabel[t]}</Text>
          </Pressable>
        );
      })}
      <View style={styles.divider} />
      <Pressable accessibilityRole="button" onPress={onClose} style={styles.close}>
        <MaterialCommunityIcons name="close" size={16} color={colors.inkMuted} />
        <Text style={styles.closeLabel}>CLOSE</Text>
      </Pressable>
    </View>
  );
}

const styles = StyleSheet.create({
  drawer: {
    position: 'absolute',
    left: 12,
    top: 12,
    width: 224,
    paddingVertical: 12,
    borderRadius: 18,
    backgroundColor: colors.surface,
    shadowColor: '#1F2430',
    shadowOpacity: 0.18,
    shadowRadius: 18,
    shadowOffset: { width: 0, height: 6 },
    elevation: 12,
  },
  heading: { ...type.overline, marginLeft: 20, marginBottom: 8 },
  item: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
    marginHorizontal: 8,
    paddingHorizontal: 12,
    paddingVertical: 11,
    borderRadius: 10,
  },
  itemSelected: { backgroundColor: colors.accent },
  itemPressed: { backgroundColor: colors.surfaceVariant },
  itemLabel: { ...type.titleSmall },
  divider: { height: 1, backgroundColor: colors.outline, marginTop: 4 },
  close: { flexDirection: 'row', alignItems: 'center', justifyContent: 'center', gap: 8, paddingTop: 12 },
  closeLabel: { ...type.overline },
});
