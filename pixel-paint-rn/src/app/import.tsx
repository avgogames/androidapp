import { router } from 'expo-router';
import { useMemo, useState } from 'react';
import { ActivityIndicator, KeyboardAvoidingView, Platform, Pressable, ScrollView, StyleSheet, Text, TextInput, View } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import {
  convertToPixelArt,
  DEFAULT_OPTIONS,
  MAX_GRID,
  MAX_IMPORT_COLORS,
  MIN_COLORS,
  MIN_GRID,
  type DecodedImage,
} from '../core';
import { pickAndDecodeImage } from '../platform/imageLoader';
import { repository } from '../platform/repository';
import { PixelArtImage } from '../ui/components/PixelArtImage';
import { SquareIconButton } from '../ui/components/SquareIconButton';
import { Stepper } from '../ui/components/Stepper';
import { colors, radius, type } from '../ui/theme';

const DEFAULT_TITLE = 'My artwork';

export default function ImportScreen() {
  const insets = useSafeAreaInsets();
  const [image, setImage] = useState<DecodedImage | null>(null);
  const [gridWidth, setGridWidth] = useState(DEFAULT_OPTIONS.gridWidth);
  const [maxColors, setMaxColors] = useState(DEFAULT_OPTIONS.maxColors);
  const [title, setTitle] = useState('');
  const [picking, setPicking] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Conversion runs on a 512 px image and a grid of at most 64 columns, so it is fast enough
  // to redo on every setting change for a live preview.
  const preview = useMemo(() => {
    if (!image) return null;
    try {
      return convertToPixelArt('preview', title.trim() || DEFAULT_TITLE, image, { gridWidth, maxColors });
    } catch {
      return null;
    }
  }, [image, gridWidth, maxColors, title]);

  const pick = async () => {
    setError(null);
    setPicking(true);
    try {
      const decoded = await pickAndDecodeImage();
      if (decoded) setImage(decoded);
    } catch {
      setError('That image could not be opened. Try another one.');
    } finally {
      setPicking(false);
    }
  };

  const create = async () => {
    if (!image || saving) return;
    setSaving(true);
    try {
      const id = `import_${Date.now().toString(36)}`;
      const art = convertToPixelArt(id, title.trim() || DEFAULT_TITLE, image, { gridWidth, maxColors });
      await repository.saveImported(art);
      router.replace({ pathname: '/color/[id]', params: { id } });
    } catch {
      setError('The artwork could not be saved. Please try again.');
      setSaving(false);
    }
  };

  return (
    <KeyboardAvoidingView style={styles.screen} behavior={Platform.OS === 'ios' ? 'padding' : undefined}>
      <View style={[styles.topBar, { paddingTop: insets.top + 8 }]}>
        <SquareIconButton icon="arrow-left" label="Back" onPress={() => router.back()} />
        <Text style={type.title} accessibilityRole="header">
          Import photo
        </Text>
      </View>
      <ScrollView contentContainerStyle={[styles.content, { paddingBottom: insets.bottom + 24 }]} keyboardShouldPersistTaps="handled">
        <View style={styles.previewCard}>
          {preview ? (
            <>
              <PixelArtImage art={preview} progress={null} revealAll style={styles.preview} />
              <Text style={type.labelSmall}>
                {preview.width} × {preview.height} cells · {preview.palette.length} colors
              </Text>
            </>
          ) : (
            <Text style={[type.body, { textAlign: 'center' }]}>
              Pick any image. It will be resized into a grid and reduced to a numbered palette.
            </Text>
          )}
          <Pressable accessibilityRole="button" onPress={pick} disabled={picking} style={[styles.button, styles.secondary]}>
            {picking ? <ActivityIndicator color={colors.accent} /> : <Text style={type.label}>{image ? 'Choose another' : 'Choose a photo'}</Text>}
          </Pressable>
        </View>

        {error ? <Text style={[type.body, { color: colors.warning }]}>{error}</Text> : null}

        <Setting label={`Grid width: ${gridWidth} cells`}>
          <Stepper label="Grid width" value={gridWidth} min={MIN_GRID} max={MAX_GRID} step={4} onChange={setGridWidth} />
        </Setting>
        <Setting label={`Colors: ${maxColors}`}>
          <Stepper label="Colors" value={maxColors} min={MIN_COLORS} max={MAX_IMPORT_COLORS} onChange={setMaxColors} />
        </Setting>
        <View style={{ gap: 8 }}>
          <Text style={type.overline}>NAME</Text>
          <TextInput
            value={title}
            onChangeText={setTitle}
            placeholder={DEFAULT_TITLE}
            placeholderTextColor={colors.inkMuted}
            maxLength={40}
            style={styles.input}
            returnKeyType="done"
          />
        </View>

        <Pressable
          accessibilityRole="button"
          accessibilityState={{ disabled: !image || saving }}
          disabled={!image || saving}
          onPress={create}
          style={({ pressed }) => [styles.button, styles.primary, (!image || saving) && { opacity: 0.4 }, pressed && { backgroundColor: colors.accentDark }]}
        >
          {saving ? <ActivityIndicator color="#FFFFFF" /> : <Text style={[type.label, { color: '#FFFFFF' }]}>Create artwork</Text>}
        </Pressable>
      </ScrollView>
    </KeyboardAvoidingView>
  );
}

function Setting({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <View style={styles.setting}>
      <Text style={[type.titleSmall, { flex: 1 }]}>{label}</Text>
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  screen: { flex: 1, backgroundColor: colors.paper },
  topBar: { flexDirection: 'row', alignItems: 'center', gap: 12, paddingHorizontal: 12, paddingBottom: 8 },
  content: { padding: 16, gap: 16 },
  previewCard: { backgroundColor: colors.surface, borderRadius: radius.lg, padding: 20, alignItems: 'center', gap: 16 },
  preview: { width: '100%', aspectRatio: 1, borderRadius: radius.sm, overflow: 'hidden', backgroundColor: colors.cellLight },
  setting: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  input: {
    height: 48,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: colors.outline,
    backgroundColor: colors.surface,
    paddingHorizontal: 14,
    fontSize: 15,
    color: colors.ink,
  },
  button: { height: 52, borderRadius: 14, alignItems: 'center', justifyContent: 'center', alignSelf: 'stretch' },
  primary: { backgroundColor: colors.accent },
  secondary: { backgroundColor: colors.surfaceVariant },
});
