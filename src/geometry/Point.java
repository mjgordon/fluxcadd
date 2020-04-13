package geometry;

import java.util.ArrayList;

import utility.Color;
import utility.PVector;
import static org.lwjgl.opengl.GL11.*;

public class Point extends Geometry {

	public PVector position;

//	public Point(int x, int y, int z) {
//		super();
//		position = new PVector(x,y,z);
//	}
	
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
		Color.setGlColor(color);
		glBegin(GL_POINTS);
		glVertex3f(position.x, position.y, position.z);
		glEnd();
		glPointSize(1);
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
