/** Coloring tools, mirroring the tool drawer in the editor reference screen. */
export const Tool = {
  /** Paints exactly the touched cell with the selected color; mistakes are allowed and shown. */
  PENCIL: 'PENCIL',
  /** Paints a square of brush-size cells, but only where the number matches. */
  BRUSH: 'BRUSH',
  /** Fills every connected cell sharing the tapped cell's number. */
  BUCKET: 'BUCKET',
  /** Clears painted cells in a brush-size square. */
  ERASER: 'ERASER',
  /** Selects the tapped cell's number, then returns to the previous tool. */
  PICKER: 'PICKER',
  /** One-finger panning; painting tools pan with two fingers. */
  HAND: 'HAND',
} as const;

export type Tool = (typeof Tool)[keyof typeof Tool];

export const TOOLS: readonly Tool[] = [Tool.PENCIL, Tool.BRUSH, Tool.BUCKET, Tool.ERASER, Tool.PICKER, Tool.HAND];

/** Whether a one-finger drag paints (true) or pans the canvas (false). */
export const paintsOnDrag = (tool: Tool): boolean =>
  tool === Tool.PENCIL || tool === Tool.BRUSH || tool === Tool.ERASER;

export const usesBrushSize = (tool: Tool): boolean => tool === Tool.BRUSH || tool === Tool.ERASER;

export const MIN_BRUSH = 1;
export const MAX_BRUSH = 5;
