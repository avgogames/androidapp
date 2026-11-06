import { mix, luminance, type Rgb } from '../core';

/** Palette taken from the editor reference: paper-white surfaces, one confident blue accent. */
export const colors = {
  accent: '#2F9BF0',
  accentDark: '#1C7ED0',
  accentSoft: '#DDEEFF',
  ink: '#1F2430',
  inkMuted: '#6B7280',
  paper: '#F3F4F7',
  surface: '#FFFFFF',
  surfaceVariant: '#EDEFF3',
  outline: '#E3E6EC',
  success: '#2EB872',
  warning: '#E5484D',
  cellLight: '#F7F8FA',
  scrim: 'rgba(17, 20, 28, 0.45)',
} as const;

const CELL_LIGHT: Rgb = 0xf7f8fa;
const CELL_DARK: Rgb = 0xc5cad3;
const CELL_HIGHLIGHT: Rgb = 0x9ccbf5;

/** The grey "unpainted" tone for a target color: darker targets read as darker paper. */
export const unpaintedTone = (target: Rgb): Rgb => mix(CELL_LIGHT, CELL_DARK, (1 - luminance(target)) * 0.85);

/** Unpainted cells of the selected number are tinted blue so they are easy to find. */
export const highlightTone = (target: Rgb): Rgb => mix(unpaintedTone(target), CELL_HIGHLIGHT, 0.65);

/** A wrongly painted cell: the paper tone washed with the wrong color. */
export const mistakeTone = (target: Rgb, painted: Rgb): Rgb => mix(unpaintedTone(target), painted, 0.55);

/** Dark or light ink, whichever reads on a palette swatch. */
export const inkOn = (color: Rgb): string => (luminance(color) > 0.6 ? colors.ink : '#FFFFFF');

export const radius = { sm: 8, md: 14, lg: 20 } as const;

export const shadow = {
  shadowColor: '#1F2430',
  shadowOpacity: 0.08,
  shadowRadius: 8,
  shadowOffset: { width: 0, height: 2 },
  elevation: 2,
} as const;

export const type = {
  headline: { fontSize: 26, fontWeight: '900', letterSpacing: -0.5, color: colors.ink },
  title: { fontSize: 16, fontWeight: '700', color: colors.ink },
  titleSmall: { fontSize: 14, fontWeight: '600', color: colors.ink },
  body: { fontSize: 14, lineHeight: 20, color: colors.inkMuted },
  label: { fontSize: 15, fontWeight: '600', color: colors.ink },
  labelSmall: { fontSize: 11, fontWeight: '500', color: colors.inkMuted },
  overline: { fontSize: 12, fontWeight: '600', letterSpacing: 0.6, color: colors.inkMuted },
} as const;
