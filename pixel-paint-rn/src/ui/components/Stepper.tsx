import { MaterialCommunityIcons } from '@expo/vector-icons';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { colors, type } from '../theme';

/** Compact − value + control; React Native has no built-in slider. */
export function Stepper({
  label,
  value,
  min,
  max,
  step = 1,
  onChange,
  format = String,
}: {
  label: string;
  value: number;
  min: number;
  max: number;
  step?: number;
  onChange: (value: number) => void;
  format?: (value: number) => string;
}) {
  const set = (v: number) => onChange(Math.min(max, Math.max(min, v)));
  return (
    <View style={styles.row} accessibilityRole="adjustable" accessibilityLabel={label} accessibilityValue={{ min, max, now: value }}>
      <StepButton icon="minus" label={`Decrease ${label}`} disabled={value <= min} onPress={() => set(value - step)} />
      <Text style={styles.value}>{format(value)}</Text>
      <StepButton icon="plus" label={`Increase ${label}`} disabled={value >= max} onPress={() => set(value + step)} />
    </View>
  );
}

function StepButton({ icon, label, disabled, onPress }: { icon: 'minus' | 'plus'; label: string; disabled: boolean; onPress: () => void }) {
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={label}
      disabled={disabled}
      onPress={onPress}
      hitSlop={6}
      style={({ pressed }) => [styles.step, pressed && { backgroundColor: colors.surfaceVariant }, disabled && { opacity: 0.35 }]}
    >
      <MaterialCommunityIcons name={icon} size={18} color={colors.ink} />
    </Pressable>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    height: 48,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: colors.outline,
    backgroundColor: colors.surface,
    paddingHorizontal: 4,
  },
  step: { width: 36, height: 36, borderRadius: 10, alignItems: 'center', justifyContent: 'center' },
  value: { ...type.titleSmall, minWidth: 28, textAlign: 'center', fontVariant: ['tabular-nums'] },
});
