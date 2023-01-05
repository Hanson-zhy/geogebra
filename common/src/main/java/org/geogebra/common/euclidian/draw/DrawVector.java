/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

/*
 * DrawVector.java
 *
 * Created on 16. Oktober 2001, 15:13
 */

package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.Previewable;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.util.DoubleUtil;

/**
 * 
 * @author Markus
 */
public class DrawVector extends Drawable implements Previewable, VectorVisibility {

	private GeoVectorND v;

	private boolean isVisible;
	private boolean labelVisible;
	private boolean traceDrawingNeeded = false;

	private final double[] coordsA = new double[2];
	private final double[] coordsB = new double[2];
	private final double[] coordsV = new double[2];
	private ArrayList<GeoPointND> points;
	private final GPoint2D endPoint = new GPoint2D();

	private DrawVectorStyle style;
	private DrawVectorProperties properties;

	/**
	 * Creates new DrawVector
	 * 
	 * @param view
	 *            view
	 * @param v
	 *            vector
	 */
	public DrawVector(EuclidianView view, GeoVectorND v) {
		this.view = view;
		this.v = v;
		geo = (GeoElement) v;
		createVectorStyle();
		update();
	}

	private void createVectorStyle() {
		this.style = new DrawDefaultVectorStyle(this, view);
	}

	/**
	 * @param view
	 *            view
	 * @param points
	 *            start point and end point
	 */
	public DrawVector(EuclidianView view, ArrayList<GeoPointND> points) {
		this.view = view;
		this.points = points;
		geo = view.getKernel().getConstruction().getConstructionDefaults()
				.getDefaultGeo(ConstructionDefaults.DEFAULT_VECTOR);
		createVectorStyle();
		updatePreview();
	}

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible) {
			return;
		}
		labelVisible = geo.isLabelVisible();

		updateStrokes(v);

		Coords coords;

		// start point in real world coords
		GeoPointND startPoint = v.getStartPoint();
		if (startPoint != null && !startPoint.isInfinite()) {
			coords = view.getCoordsForView(startPoint.getInhomCoordsInD3());
			if (!DoubleUtil.isZero(coords.getZ())) {
				isVisible = false;
				return;
			}
			coordsA[0] = coords.getX();
			coordsA[1] = coords.getY();
		} else {
			coordsA[0] = 0;
			coordsA[1] = 0;
		}

		// vector
		coords = view.getCoordsForView(v.getCoordsInD3());
		if (!DoubleUtil.isZero(coords.getZ())) {
			isVisible = false;
			return;
		}
		coordsV[0] = coords.getX();
		coordsV[1] = coords.getY();

		// end point
		coordsB[0] = coordsA[0] + coordsV[0];
		coordsB[1] = coordsA[1] + coordsV[1];

		// set line and arrow of vector and converts all coords to screen
		properties = getProperties();
		style.update(properties);

		// label position
		if (labelVisible) {
			labelDesc = geo.getLabelDescription();
			// note that coordsV was normalized in setArrow()
			xLabel = (int) ((coordsA[0] + coordsB[0]) / 2.0 + coordsV[1]);
			yLabel = (int) ((coordsA[1] + coordsB[1]) / 2.0 - coordsV[0]);
			addLabelOffset();
		}

		// draw trace
		// a vector is a Locateable and it might
		// happen that there are several update() calls
		// before the new trace should be drawn
		// so the actual drawing is moved to draw()
		traceDrawingNeeded = v.getTrace();
		if (v.getTrace()) {
			isTracing = true;
		} else {
			if (isTracing) {
				isTracing = false;
				// view.updateBackground();
			}
		}
	}

	private DrawVectorProperties getProperties() {
		return new DrawVectorProperties(coordsA, coordsB, coordsV, v.getLineThickness(),
				objStroke);
	}

	/**
	 * @param lineThickness
	 *            vector thickness
	 * @return arrow size
	 */
	static final public double getFactor(double lineThickness) {

		// changed to make arrow-heads a bit bigger for line thickness 8-13
		return lineThickness < 8 ? 12.0 + lineThickness : 3 * lineThickness;
	}


	@Override
	public void draw(GGraphics2D g2) {
		if (!isVisible) {
			return;
		}

		if (traceDrawingNeeded) {
			drawTraceToBackground();
		}

		if (isHighlighted()) {
			drawHighlight(g2);
		}

		drawVectorShape(g2);

		if (labelVisible) {
			drawVectorLabel(g2);
		}

	}

	private void drawTraceToBackground() {
		traceDrawingNeeded = false;
		drawTrace(view.getBackgroundGraphics());
	}

	@Override
	protected final void drawTrace(GGraphics2D g2) {
		if (g2 == null) {
			return;
		}

		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);
		g2.fill(style.getShape());
	}

	private void drawHighlight(GGraphics2D g2) {
		g2.setPaint(v.getSelColor());
		g2.setStroke(selStroke);
		if (isVisible) {
			g2.draw(style.getShape());
		}
	}

	private void drawVectorShape(GGraphics2D g2) {
		g2.setPaint(getObjectColor());
		g2.setStroke(objStroke);

		style.draw(g2);
	}

	private void drawVectorLabel(GGraphics2D g2) {
		g2.setFont(view.getFontVector());
		g2.setPaint(v.getLabelColor());
		drawLabel(g2);
	}

	@Override
	final public void updatePreview() {
		isVisible = points.size() == 1;
		if (isVisible) {
			// start point
			view.getCoordsForView(points.get(0).getInhomCoordsInD3())
					.get(coordsA);
			coordsB[0] = coordsA[0];
			coordsB[1] = coordsA[1];
		}
	}

	@Override
	final public void updateMousePos(double xRWmouse, double yRWmouse) {
		double xRW = xRWmouse;
		double yRW = yRWmouse;
		if (isVisible) {
			// double 	xRW = view.toRealWorldCoordX(x);
			// double yRW = view.toRealWorldCoordY(y);

			// round angle to nearest 15 degrees if alt pressed
			if (points.size() == 1
					&& view.getEuclidianController().isAltDown()) {
				GeoPointND p = points.get(0);
				double px = p.getInhomX();
				double py = p.getInhomY();
				double angle = Math.atan2(yRW - py, xRW - px) * 180 / Math.PI;
				double radius = Math.sqrt(
						(py - yRW) * (py - yRW) + (px - xRW) * (px - xRW));

				// round angle to nearest 15 degrees
				angle = Math.round(angle / 15) * 15;

				xRW = px + radius * Math.cos(angle * Math.PI / 180);
				yRW = py + radius * Math.sin(angle * Math.PI / 180);

				endPoint.setLocation(xRW, yRW);
				view.getEuclidianController().setLineEndPoint(endPoint);
			} else {
				view.getEuclidianController().setLineEndPoint(null);
			}

			// set start and end point in real world coords
			// GeoPoint P = (GeoPoint) points.get(0);
			// P.getInhomCoords(coordsA);
			if (points.size() > 0) {
				view.getCoordsForView(points.get(0).getInhomCoordsInD3())
						.get(coordsA);
			}

			coordsB[0] = xRW;
			coordsB[1] = yRW;
			style.update(getProperties());
		}
	}

	@Override
	final public void drawPreview(GGraphics2D g2) {
		if (isVisible) {
			g2.setPaint(getObjectColor());
			updateStrokes(geo);
			g2.setStroke(objStroke);
			g2.fill(style.getShape());
		}
	}

	@Override
	public void disposePreview() {
		// do nothing
	}

	@Override
	final public boolean hit(int x, int y, int hitThreshold) {
		return style.getShape().intersects(x - 3, y - 3, 6, 6);
	}

	@Override
	final public boolean isInside(GRectangle rect) {
		return rect.contains(style.getShape().getBounds());
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		return rect.intersects(style.getShape().getBounds());
	}

	/**
	 * Returns the bounding box of this Drawable in screen coordinates.
	 */
	@Override
	final public GRectangle getBounds() {
		return style.getShape().getBounds();
	}


	@Override
	public void setVisible(boolean visible) {
		this.isVisible = visible;
	}

	@Override
	public boolean isVisible() {
		return isVisible;
	}
}
