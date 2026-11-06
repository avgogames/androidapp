import { Alert, Platform } from 'react-native';

/**
 * A destructive-action confirmation. React Native's Alert does nothing on the web, so the web
 * preview falls back to the browser's confirm dialog.
 */
export function confirmDestructive(title: string, message: string, action: string, onConfirm: () => void): void {
  if (Platform.OS === 'web') {
    if (globalThis.confirm?.(`${title}\n\n${message}`)) onConfirm();
    return;
  }
  Alert.alert(title, message, [
    { text: 'Cancel', style: 'cancel' },
    { text: action, style: 'destructive', onPress: onConfirm },
  ]);
}
