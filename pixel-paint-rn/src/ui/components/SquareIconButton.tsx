import { MaterialCommunityIcons } from '@expo/vector-icons';
import type { ComponentProps } from 'react';
import { Pressable, StyleSheet } from 'react-native';
import { colors, shadow } from '../theme';

export type IconName = ComponentProps<typeof MaterialCommunityIcons>['name'];

/** White rounded-square icon button with a soft shadow, as in the editor reference. */
export function SquareIconButton({
  icon,
  label,
  onPress,
  disabled = false,
  active = false,
  size = 44,
}: {
  icon: IconName;
  label: string;
  onPress: () => void;
  disabled?: boolean;
  active?: boolean;
  size?: number;
}) {
  return (
    <Pressable
      accessibilityRole="button"
      accessibilityLabel={label}
      accessibilityState={{ disabled }}
      disabled={disabled}
      onPress={onPress}
      hitSlop={4}
      style={({ pressed }) => [
        styles.button,
        { width: size, height: size, borderRadius: size * 0.27 },
        active && styles.active,
        pressed && styles.pressed,
        disabled && styles.disabled,
      ]}
    >
      <MaterialCommunityIcons name={icon} size={size * 0.5} color={active ? '#FFFFFF' : colors.ink} />
    </Pressable>
  );
}

const styles = StyleSheet.create({
  button: {
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.surface,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.outline,
    ...shadow,
  },
  active: { backgroundColor: colors.accent, borderColor: colors.accent },
  pressed: { transform: [{ scale: 0.96 }], opacity: 0.85 },
  disabled: { opacity: 0.35 },
});
