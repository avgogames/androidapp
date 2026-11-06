import { Canvas, Group, Picture, useFont } from '@shopify/react-native-skia';
import { useEffect, useMemo, useRef, useState } from 'react';
import { PanResponder, Platform, StyleSheet, View, type GestureResponderEvent, type LayoutChangeEvent } from 'react-native';
import { paintsOnDrag, type ColoringProgress, type PixelArt, type Tool } from '../../core';
import { NUMBER_FONT_SIZE, recordCells, recordGrid, recordNumbers, UNIT } from './gridPictures';

// Bundled so numbers look the same on Android, iOS and the web preview.
const NUMBER_FONT = require('@expo-google-fonts/inter/600SemiBold/Inter_600SemiBold.ttf');
import { cellAt, FIT_VIEWPORT, gridLayout, transformViewport, type Point, type Size, type Viewport } from './gridViewport';

const TOUCH_SLOP = 8;
const NUMBER_THRESHOLD = 14;
const GRID_THRESHOLD = 7;
const COMFORTABLE_CELL = 36;

export interface PixelGridProps {
  art: PixelArt;
  progress: ColoringProgress;
  selectedColor: number;
  tool: Tool;
  /** Changing this number snaps the canvas back to fit the screen. */
  fitKey: number;
  onTap: (cell: number) => void;
  onStrokeStart: () => void;
  onStrokeMove: (cell: number) => void;
  onStrokeEnd: () => void;
}

interface TouchInfo {
  count: number;
  centroid: Point;
  distance: number;
}

function touchInfo(e: GestureResponderEvent): TouchInfo {
  const touches = e.nativeEvent.touches.length ? e.nativeEvent.touches : [e.nativeEvent];
  const a = touches[0];
  const b = touches[1];
  if (!b) return { count: 1, centroid: { x: a.locationX, y: a.locationY }, distance: 0 };
  return {
    count: touches.length,
    centroid: { x: (a.locationX + b.locationX) / 2, y: (a.locationY + b.locationY) / 2 },
    distance: Math.hypot(a.locationX - b.locationX, a.locationY - b.locationY),
  };
}

/**
 * The paintable canvas. One finger paints with drawing tools (or pans with the others), two
 * fingers always zoom and pan. A touch that never passes the touch slop is a tap.
 *
 * Cells, grid and numbers are recorded once per change as Skia pictures in art space, so a
 * pinch only changes one transform instead of redrawing thousands of cells from JavaScript.
 */
export function PixelGrid(props: PixelGridProps) {
  const { art, progress, selectedColor, fitKey } = props;
  const [size, setSize] = useState<Size>({ width: 0, height: 0 });
  const [viewport, setViewport] = useState<Viewport>(FIT_VIEWPORT);

  useEffect(() => setViewport(FIT_VIEWPORT), [fitKey, art]);

  const cellsPicture = useMemo(() => recordCells(art, progress, selectedColor), [art, progress, selectedColor]);
  const gridPicture = useMemo(() => recordGrid(art), [art]);
  const font = useFont(NUMBER_FONT, NUMBER_FONT_SIZE);
  const numbersPicture = useMemo(
    () => (progress.isComplete || !font ? null : recordNumbers(art, progress, selectedColor, font)),
    [art, progress, selectedColor, font],
  );

  // The responder is created once, so it reads everything current through this ref.
  const live = useRef({ props, size, viewport });
  live.current = { props, size, viewport };

  const responder = useMemo(() => {
    let start: Point = { x: 0, y: 0 };
    let previous: TouchInfo | null = null;
    let multiTouch = false;
    let pastSlop = false;
    let stroking = false;
    let lastCell: number | null = null;

    const cellUnder = (point: Point) => {
      const { viewport: v, size: s, props: p } = live.current;
      return cellAt(v, s, p.art, point);
    };
    const moveStroke = (point: Point) => {
      const cell = cellUnder(point);
      if (cell !== null && cell !== lastCell) {
        lastCell = cell;
        live.current.props.onStrokeMove(cell);
      }
    };
    const endStroke = () => {
      if (stroking) live.current.props.onStrokeEnd();
      stroking = false;
      lastCell = null;
    };
    const pan = (zoom: number, centroid: Point, delta: Point) => {
      setViewport((v) => {
        const { size: s, props: p } = live.current;
        return transformViewport(v, s, p.art, centroid, zoom, delta, COMFORTABLE_CELL);
      });
    };

    return PanResponder.create({
      onStartShouldSetPanResponder: () => true,
      onMoveShouldSetPanResponder: () => true,
      onPanResponderTerminationRequest: () => false,
      onShouldBlockNativeResponder: () => true,
      onPanResponderGrant: (e) => {
        previous = touchInfo(e);
        start = previous.centroid;
        multiTouch = previous.count >= 2;
        pastSlop = false;
        stroking = false;
        lastCell = null;
      },
      onPanResponderMove: (e) => {
        const info = touchInfo(e);
        const prev = previous;
        previous = info;
        if (info.count >= 2 && !multiTouch) {
          multiTouch = true;
          endStroke();
          return;
        }
        // Finger count changed: re-anchor so the canvas doesn't jump.
        if (!prev || prev.count !== info.count) return;

        const drawing = paintsOnDrag(live.current.props.tool) && !multiTouch;
        if (!drawing) {
          const moved = Math.hypot(info.centroid.x - start.x, info.centroid.y - start.y);
          if (multiTouch || pastSlop || moved > TOUCH_SLOP) {
            pastSlop = true;
            const zoom = info.count >= 2 && prev.distance > 0 ? info.distance / prev.distance : 1;
            pan(zoom, info.centroid, { x: info.centroid.x - prev.centroid.x, y: info.centroid.y - prev.centroid.y });
          }
          return;
        }
        if (!pastSlop && Math.hypot(info.centroid.x - start.x, info.centroid.y - start.y) > TOUCH_SLOP) {
          pastSlop = true;
          stroking = true;
          live.current.props.onStrokeStart();
          moveStroke(start);
        }
        if (stroking) moveStroke(info.centroid);
      },
      onPanResponderRelease: () => {
        endStroke();
        if (!pastSlop && !multiTouch) {
          const cell = cellUnder(start);
          if (cell !== null) live.current.props.onTap(cell);
        }
      },
      onPanResponderTerminate: endStroke,
    });
  }, []);

  const onLayout = (e: LayoutChangeEvent) => {
    const { width, height } = e.nativeEvent.layout;
    setSize({ width, height });
  };

  const { cell, origin } = gridLayout(viewport, size, art);
  const ready = size.width > 0 && size.height > 0;
  const scale = cell / UNIT;
  const complete = progress.isComplete;

  return (
    <View style={styles.container} onLayout={onLayout} {...responder.panHandlers}>
      {ready ? (
        <View style={StyleSheet.absoluteFill} pointerEvents="none">
          <Canvas style={StyleSheet.absoluteFill}>
            <Group transform={[{ translateX: origin.x }, { translateY: origin.y }, { scale }]}>
              <Picture picture={cellsPicture} />
              {!complete && cell >= GRID_THRESHOLD ? <Picture picture={gridPicture} /> : null}
              {numbersPicture && cell >= NUMBER_THRESHOLD ? <Picture picture={numbersPicture} /> : null}
            </Group>
          </Canvas>
        </View>
      ) : null}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    overflow: 'hidden',
    // Web preview: stop the browser's own pinch-zoom and scrolling from taking the gesture.
    ...(Platform.OS === 'web' ? ({ touchAction: 'none', userSelect: 'none' } as object) : null),
  },
});
