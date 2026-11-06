import { Modal, Pressable, StyleSheet, Text, View } from 'react-native';
import type { PixelArt } from '../../core';
import { PixelArtImage } from '../components/PixelArtImage';
import { colors, radius, type } from '../theme';

export function CompletionDialog({
  art,
  visible,
  onGallery,
  onStay,
}: {
  art: PixelArt;
  visible: boolean;
  onGallery: () => void;
  onStay: () => void;
}) {
  return (
    <Modal transparent visible={visible} animationType="fade" onRequestClose={onStay} statusBarTranslucent>
      <View style={styles.scrim}>
        <View style={styles.card}>
          <PixelArtImage art={art} progress={null} revealAll style={styles.preview} />
          <Text style={type.headline}>Masterpiece!</Text>
          <Text style={[type.body, { textAlign: 'center' }]}>You finished “{art.title}”.</Text>
          <View style={styles.actions}>
            <Pressable accessibilityRole="button" onPress={onStay} style={[styles.button, styles.secondary]}>
              <Text style={type.label}>Admire it</Text>
            </Pressable>
            <Pressable accessibilityRole="button" onPress={onGallery} style={[styles.button, styles.primary]}>
              <Text style={[type.label, { color: '#FFFFFF' }]}>Back to gallery</Text>
            </Pressable>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  scrim: { flex: 1, backgroundColor: colors.scrim, alignItems: 'center', justifyContent: 'center', padding: 24 },
  card: {
    width: '100%',
    maxWidth: 360,
    backgroundColor: colors.surface,
    borderRadius: radius.lg + 4,
    padding: 24,
    alignItems: 'center',
    gap: 8,
  },
  preview: { width: 180, height: 180, marginBottom: 8, borderRadius: radius.sm, overflow: 'hidden' },
  actions: { flexDirection: 'row', gap: 10, marginTop: 12, alignSelf: 'stretch' },
  button: { flex: 1, height: 48, borderRadius: 12, alignItems: 'center', justifyContent: 'center' },
  primary: { backgroundColor: colors.accent },
  secondary: { backgroundColor: colors.surfaceVariant },
});
