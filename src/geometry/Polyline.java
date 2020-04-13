package geometry;

import java.util.ArrayList;

import utility.Color;
import utility.PVector;
import static org.lwjgl.opengl.GL11.*;

public class Polyline extends Geometry {

	public float fillR;
	public float fillG;
	public float fillB;

	public boolean stroked = true;
	public boolean filled = false;

	private ArrayList<PVector> vertices;
	protected ArrayList<Line> hatchLines;
	
	protected boolean closed = false;

	public Polyline() {
		super();
		this.vertices = new ArrayList<PVector>();
	}
	
	public Polyline(ArrayList<PVector> vertices) {
		super();
		this.vertices = vertices;
	}

	public Polyline(ArrayList<PVector> vertices, float fillR, float fillG, float fillB) {
		super();
		this.vertices = vertices;
		this.fillR = fillR;
		this.fillG = fillG;
		this.fillB = fillB;
	}

	public void setVertices(ArrayList<PVector> vertices) {
		this.vertices = vertices;
	}
	
	
	public void generateHatchingLines() {
		ArrayList<Line> lines = new ArrayList<Line>();
		for (int i = 0; i < vertices.size() - 1; i++) {
			Line l = new Line(vertices.get(i), vertices.get(i + 1));
			l.setColor(color);
			lines.add(l);
		}
		// Close the shape
		Line l = new Line(vertices.get(vertices.size() - 1), vertices.get(0));
		
		l.setColor(color);
		lines.add(l);
		
		// Generate hatching lines
		for (int y = (int) (position.y - size.y); y < position.x + (size.y); y += 10) {
			ArrayList<PVector> intersections = new ArrayList<PVector>();
			// Find intersections
			for (Line line : lines) {
				float x = line.xValueAtY(y);
				if (line.containsX(x)) {
					intersections.add(new PVector(x, y));
				}
			}
			// Sort intersections left to right.
			ArrayList<PVector> sortedIntersections = new ArrayList<PVector>();
			while (intersections.size() > 0) {
				float mostLeftValue = Float.MAX_VALUE;
				PVector mostLeftPoint = null;
				for (PVector v : intersections) {
					if (mostLeftPoint == null || v.x < mostLeftValue) {
						mostLeftPoint = v;
						mostLeftValue = v.x;
					}
				}
				sortedIntersections.add(mostLeftPoint);
				intersections.remove(mostLeftPoint);
			}
			// Create lines
			for (int i = 0; i < sortedIntersections.size() / 2; i++) {
				Line hatch = new Line(sortedIntersections.get(i * 2), sortedIntersections.get(i * 2 + 1));
				hatch.setColor(color);
				hatchLines.add(hatch);
			}
		}
	}
	
	@Override
	public void render() {
		if (!visible)
			return;
		if (filled) {
			glColor3f(fillR, fillG, fillB);
			glBegin(GL_POLYGON);
			for (PVector v : vertices) {
				glVertex2f(v.x, v.y);
			}
			glEnd();
		}

		if (stroked) {
			Color.setGlColor(color);
			glBegin((closed) ? GL_LINE_LOOP : GL_LINE_STRIP);
			for (PVector v : vertices) {
				glVertex2f(v.x, v.y);
			}
			glEnd();
		}
	}

	@Override
	public ArrayList<PVector> getVectorRepresentation(float resolution) {
		ArrayList<PVector> out = new ArrayList<PVector>();
		for (PVector v : vertices) {
			out.add(v.copy());
		}
		return (out);
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>(hatchLines));
	}

}
