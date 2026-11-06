import { describe, expect, it } from '@jest/globals';
import { ColoringEditor, ColoringProgress, ColoringSession, createPixelArt, Category, EditHistory, Tool, UNPAINTED } from '..';

// 4x3 grid:
// 0 0 1 1
// 0 1 1 0
// 2 2 2 0
const art = createPixelArt({
  id: 'e',
  title: 'Editor',
  category: Category.SCENES,
  width: 4,
  height: 3,
  palette: [0x112233, 0x445566, 0x778899],
  cells: [0, 0, 1, 1, 0, 1, 1, 0, 2, 2, 2, 0],
});
const editor = new ColoringEditor(art);

describe('ColoringEditor', () => {
  it('pencil paints any color but never overwrites a correct cell', () => {
    let p = ColoringProgress.empty(art);
    expect([...editor.changesFor(Tool.PENCIL, 0, 2, 1, p)]).toEqual([[0, 2]]);
    p = p.with(art, new Map([[0, 0]]));
    expect(editor.changesFor(Tool.PENCIL, 0, 2, 1, p).size).toBe(0);
  });

  it('brush paints only matching numbers inside the square', () => {
    const changes = editor.changesFor(Tool.BRUSH, 5, 1, 3, ColoringProgress.empty(art));
    expect([...changes.keys()].sort((a, b) => a - b)).toEqual([2, 5, 6]);
  });

  it('bucket fills the 4-connected region only', () => {
    const changes = editor.changesFor(Tool.BUCKET, 0, 0, 1, ColoringProgress.empty(art));
    expect([...changes.keys()].sort((a, b) => a - b)).toEqual([0, 1, 4]);
  });

  it('eraser clears painted cells', () => {
    const p = ColoringProgress.empty(art).with(art, new Map([[0, 0], [1, 2]]));
    expect([...editor.changesFor(Tool.ERASER, 0, 0, 2, p)]).toEqual([[0, UNPAINTED], [1, UNPAINTED]]);
  });

  it('joins fast drags without gaps', () => {
    expect(editor.cellsBetween(0, 3)).toEqual([0, 1, 2, 3]);
    expect(editor.cellsBetween(0, 10)).toHaveLength(3);
  });
});

describe('EditHistory', () => {
  it('undoes and redoes, and a new edit clears redo', () => {
    const h = new EditHistory(2);
    h.record({ before: new Map([[0, -1]]), after: new Map([[0, 1]]) });
    expect(h.undo()).toEqual(new Map([[0, -1]]));
    expect(h.redo()).toEqual(new Map([[0, 1]]));
    h.undo();
    h.record({ before: new Map([[1, -1]]), after: new Map([[1, 1]]) });
    expect(h.canRedo).toBe(false);
  });
});

describe('ColoringSession', () => {
  it('makes one stroke one undo step and persists each commit', () => {
    const saved: ColoringProgress[] = [];
    const s = new ColoringSession(art, ColoringProgress.empty(art), (p) => saved.push(p));
    expect(s.getState().selectedColor).toBe(0);
    s.selectTool(Tool.BRUSH);
    s.setBrushSize(1);
    s.strokeStart();
    s.strokeMove(0);
    s.strokeMove(1);
    s.strokeEnd();
    expect(s.getState().progress.correctCount).toBe(2);
    expect(saved).toHaveLength(1);
    s.undo();
    expect(s.getState().progress.correctCount).toBe(0);
    s.redo();
    expect(s.getState().progress.correctCount).toBe(2);
  });

  it('jumps to the next unfinished color and celebrates completion', () => {
    const s = new ColoringSession(art, ColoringProgress.empty(art), () => {});
    s.selectTool(Tool.BUCKET);
    s.tap(0); // region {0,1,4}
    s.tap(7); // region {7,11}
    expect(s.getState().selectedColor).toBe(1);
    s.tap(2);
    expect(s.getState().selectedColor).toBe(2);
    s.tap(8);
    expect(s.getState().progress.isComplete).toBe(true);
    expect(s.getState().showCompletion).toBe(true);
  });

  it('picker selects the number and returns to the previous tool', () => {
    const s = new ColoringSession(art, ColoringProgress.empty(art), () => {});
    s.selectTool(Tool.BRUSH);
    s.selectTool(Tool.PICKER);
    s.tap(9);
    expect(s.getState().selectedColor).toBe(2);
    expect(s.getState().tool).toBe(Tool.BRUSH);
  });

  it('reset clears progress and history', () => {
    const s = new ColoringSession(art, ColoringProgress.empty(art), () => {});
    s.tap(0);
    s.reset();
    expect(s.getState().progress.isStarted).toBe(false);
    expect(s.getState().canUndo).toBe(false);
  });
});
