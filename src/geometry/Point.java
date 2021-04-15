package geometry;

import java.util.ArrayList;

import utility.Color;
import utility.PVector;
import static org.lwjgl.opengl.GL11.*;

public class Point extends Geometry {

	private PVector position;
	
	public Point(float x, float y, float z) {
		super();
		position = new PVector(x, y, z);
	}

	public Point(PVector v) {
		super();
		position = new PVector(v.x, v.y, v.z);
	}

	@Override
	public void render() {
		if (!visible)
			return;
		glPointSize(4);
		if (color != null) {
			Color.setGlColor(color);
			glBegin(GL_POINTS);
			glVertex3f(position.x, position.y, position.z);
			glEnd();
			glPointSize(1);
		}

	}
	
	public float x() {
		return position.x;
	}
	
	public float y() {
		return position.y;
	}
	
	public float z() {
		return position.z;
	}
	
	/**
	 * Gets a copy of the internal vector object
	 * @return
	 */
	public PVector getVector() {
		return position.copy();
	}
	
	public float dist(Point point) {
		float dx = x() - point.x();
		float dy = y() - point.y();
		float dz = z() - point.z();
		
		return((float)Math.sqrt( (dx * dx) + (dy * dy) + (dz * dz)));
	}

	@Override
	public ArrayList<PVector> getVectorRepresentation(float resolution) {
		ArrayList<PVector> out = new ArrayList<PVector>();
		out.add(position.copy());
		return (out);
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>());
	}
}
