package geometry;

import java.util.ArrayList;
import java.util.Arrays;

import utility.Color;
import utility.PVector;
import utility.Util;

import static org.lwjgl.opengl.GL11.*;

import jsint.Pair;

public class Polyline extends Curve {

	public boolean stroked = true;
	public boolean filled = false;

	
	private ArrayList<Point> vertices = null;;
	
	private PVector[] explicitVertices = new PVector[0];
	
	
	
	protected ArrayList<Line> hatchLines;
	
	protected boolean closed = false;
	
	private float[] segmentLengths;
	private float calculatedLength = -1;
	

	public Polyline() {
		super();
		this.vertices = new ArrayList<Point>();
		recalculateExplicitGeometry();
	}
	
	public Polyline(Point[] vertices) {
		super();
		this.vertices = new ArrayList<Point>(Arrays.asList(vertices));
		recalculateExplicitGeometry();
	}
	
//	public Polyline(ArrayList<Point> vertices) {
//		super();
//		this.vertices = vertices;
//	}
	
	public Polyline(PVector[] explicitVertices) {
		this.explicitVertices = explicitVertices;
	}
	
	public Polyline(ArrayList<PVector> explicitVertices) {
		this.explicitVertices = explicitVertices.toArray(new PVector[explicitVertices.size()]);
	}
	
	public Polyline(Pair pair) {
		super();
		this.vertices = new ArrayList<Point>();	
		while(pair.first != null && pair != Pair.EMPTY) {
			vertices.add((Point) pair.first);
			pair = (Pair)pair.rest;
		}	
		
		recalculateExplicitGeometry();
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
	public void render() {
		if (!visible)
			return;
		if (filled) {
			Color.setGlColor(color);
			glBegin(GL_POLYGON);
			for (PVector v : explicitVertices) {
				glVertex2f(v.x, v.y);
			}
			glEnd();
		}

		if (stroked) {
			Color.setGlColor(color);
			glBegin((closed) ? GL_LINE_LOOP : GL_LINE_STRIP);
			for (PVector v : explicitVertices) {
				glVertex2f(v.x, v.y);
			}
			glEnd();
		}
	}

	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>(hatchLines));
	}
	

	

	
	@Override 
	public PVector getVectorOnCurve(float p) {
		recalculateLength();
		
		float scaledPos = p * calculatedLength;
		
		for (int i = 0; i < segmentLengths.length; i++) {
			if (scaledPos < segmentLengths[i]) {
				return PVector.lerp(vertices.get(i).getVector(), vertices.get(i + 1).getVector(), scaledPos / segmentLengths[i]);
			}
			else {
				scaledPos -= segmentLengths[i];
			}
		}
		System.out.println("Bad polyline parameter : " + p);
		return(null);
	}
	
	private void recalculateLength() {
		segmentLengths = new float[vertices.size() - 1];
		for (int i = 0; i < vertices.size() - 1; i++) {
			segmentLengths[i] = vertices.get(i).dist(vertices.get(i + 1));
		}
		calculatedLength = Util.arraySum(segmentLengths);
	}

	@Override
	/**
	 * Polylines are their own explicit geometry. 
	 */
	public void recalculateExplicitGeometry() {
		explicitGeometry = this;
		if (vertices != null) {
			
			explicitVertices = new PVector[vertices.size()];
			for (int i = 0; i < vertices.size();i ++) {
				explicitVertices[i] = vertices.get(i).getVector();
			}	
		}	
	}
}
