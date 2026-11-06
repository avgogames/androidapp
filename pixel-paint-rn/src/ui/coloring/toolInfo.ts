import { Tool } from '../../core';
import type { IconName } from '../components/SquareIconButton';

export const toolLabel: Record<Tool, string> = {
  PENCIL: 'Pencil',
  BRUSH: 'Brush',
  BUCKET: 'Bucket',
  ERASER: 'Eraser',
  PICKER: 'Color Picker',
  HAND: 'Hand',
};

export const toolIcon: Record<Tool, IconName> = {
  PENCIL: 'pencil',
  BRUSH: 'brush',
  BUCKET: 'format-color-fill',
  ERASER: 'eraser',
  PICKER: 'eyedropper-variant',
  HAND: 'hand-back-right-outline',
};

export const isTool = (t: string): t is Tool => t in Tool;
