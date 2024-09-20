package geometry;

import java.util.ArrayList;
import java.util.Arrays;

import jsint.Pair;
import render_sdf.animation.Matrix4dAnimated;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;
import utility.Util;

public class Polyline extends Curve {

	public boolean stroked = true;
	public boolean filled = false;

	private ArrayList<Point> vertices = null;

	protected ArrayList<Line> hatchLines;

	protected boolean closed = false;

	private double[] segmentLengths;
	private double calculatedLength = -1;


	public Polyline() {
		super();
		this.vertices = new ArrayList<Point>();
		setMatrix(new Matrix4dAnimated(new Matrix4d(),"Polyline"));
	}


	public Polyline(Point[] vertices) {
		super();
		this.vertices = new ArrayList<Point>(Arrays.asList(vertices));
		recalculateExplicitGeometry();
		setMatrix(new Matrix4dAnimated(new Matrix4d(),"Polyline"));
	}


	public Polyline(Vector3d[] explicitVertices) {
		this.explicitVectors = explicitVertices;
		setMatrix(new Matrix4dAnimated(new Matrix4d(),"Polyline"));
	}


	public Polyline(ArrayList<Vector3d> explicitVertices) {
		this.explicitVectors = explicitVertices.toArray(new Vector3d[explicitVertices.size()]);
		setMatrix(new Matrix4dAnimated(new Matrix4d(),"Polyline"));
	}


	public Polyline(Pair pair) {
		super();
		this.vertices = new ArrayList<Point>();
		while (pair.first != null && pair != Pair.EMPTY) {
			vertices.add((Point) pair.first);
			pair = (Pair) pair.rest;
		}

		recalculateExplicitGeometry();
		setMatrix(new Matrix4dAnimated(new Matrix4d(),"Polyline"));
	}


	public void setVertices(ArrayList<Point> vertices) {
		this.vertices = vertices;
	}

//	public void generateHatchingLines() {
//		ArrayList<Line> lines = new ArrayList<Line>();
//		for (int i = 0; i < vertices.size() - 1; i++) {
//			Line l = new Line(vertices.get(i), vertices.get(i + 1));
//			l.setColor(color);
//			lines.add(l);
//		}
//		// Close the shape
//		Line l = new Line(vertices.get(vertices.size() - 1), vertices.get(0));
//		
//		l.setColor(color);
//		lines.add(l);
//		
//		// Generate hatching lines
//		for (int y = (int) (frame.m13 - size.y); y < frame.m03 + (size.y); y += 10) {
//			ArrayList<PVector> intersections = new ArrayList<PVector>();
//			// Find intersections
//			for (Line line : lines) {
//				float x = line.xValueAtY(y);
//				if (line.containsX(x)) {
//					intersections.add(new PVector(x, y));
//				}
//			}
//			// Sort intersections left to right.
//			ArrayList<PVector> sortedIntersections = new ArrayList<PVector>();
//			while (intersections.size() > 0) {
//				float mostLeftValue = Float.MAX_VALUE;
//				PVector mostLeftPoint = null;
//				for (PVector v : intersections) {
//					if (mostLeftPoint == null || v.x < mostLeftValue) {
//						mostLeftPoint = v;
//						mostLeftValue = v.x;
//					}
//				}
//				sortedIntersections.add(mostLeftPoint);
//				intersections.remove(mostLeftPoint);
//			}
//			// Create lines
//			for (int i = 0; i < sortedIntersections.size() / 2; i++) {
//				Line hatch = new Line(sortedIntersections.get(i * 2), sortedIntersections.get(i * 2 + 1));
//				hatch.setColor(color);
//				hatchLines.add(hatch);
//			}
//		}
//	}


	@Override
	public void render(double time) {
		if (!visible) {
			return;
		}
		
		GL11.glPushMatrix();
		GL11.glMultMatrixd(matrix.getArray(time));
		

		if (filled) {
			OGLWrapper.glColor(colorFill);
			GL11.glBegin(GL11.GL_POLYGON);
			for (Vector3d v : explicitVectors) {
				OGLWrapper.glVertex(v);
			}
			GL11.glEnd();
		}

		if (stroked) {
			OGLWrapper.glColor(colorFill);
			GL11.glBegin((closed) ? GL11.GL_LINE_LOOP : GL11.GL_LINE_STRIP);
			for (Vector3d v : explicitVectors) {
				OGLWrapper.glVertex(v);
			}
			GL11.glEnd();
		}
		
		GL11.glPopMatrix();
	}


	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>(hatchLines));
	}


	@Override
	public Vector3d getLocalVectorOnCurve(double t, double time) {
		recalculateLength(time);

		double scaledPos = t * calculatedLength;

		for (int i = 0; i < segmentLengths.length; i++) {
			if (scaledPos < segmentLengths[i]) {
				return vertices.get(i).getVector(time).lerp(vertices.get(i + 1).getVector(time), scaledPos / segmentLengths[i]);
			}
			else {
				scaledPos -= segmentLengths[i];
			}
		}
		System.out.println("Bad polyline parameter : " + t);
		return (null);
	}


	private void recalculateLength(double time) {
		segmentLengths = new double[vertices.size() - 1];
		for (int i = 0; i < vertices.size() - 1; i++) {
			segmentLengths[i] = vertices.get(i).dist(vertices.get(i + 1),time);
		}
		calculatedLength = Util.arraySum(segmentLengths);
	}


	/**
	 * Polylines are their own explicit geometry.
	 */
	@Override
	public void recalculateExplicitGeometry() {
		explicitGeometry = this;
		
		if (vertices != null) {
			explicitVectors = new Vector3d[vertices.size()];
			for (int i = 0; i < explicitVectors.length; i++) {
				explicitVectors[i] = vertices.get(i).getVector(0);
			}
		}
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}
}
