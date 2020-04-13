package geometry;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_POLYGON;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.ArrayList;

import utility.Color;
import utility.PVector;

public class Polycurve extends Geometry {

	public boolean stroked = true;
	public boolean filled = false;

	public float fillR;
	public float fillG;
	public float fillB;

	private ArrayList<Polyline> pieces;

	public Polycurve() {
		pieces = new ArrayList<Polyline>();
	}

	@Override
	public void render() {
		if (!visible)
			return;
		ArrayList<PVector> allPoints = getVectorRepresentation(10);
		if (filled) {
			glColor3f(fillR, fillG, fillB);
			glBegin(GL_POLYGON);
		} else {
			Color.setGlColor(color);
			glBegin(GL_LINE_STRIP);
		}

		for(PVector v : allPoints) {
			glVertex2f(v.x,v.y);
		}
		glEnd();

	}

	@Override
	public ArrayList<PVector> getVectorRepresentation(float resolution) {
		ArrayList<PVector> allPoints = new ArrayList<PVector>();
		for (Polyline l : pieces) {
			allPoints.addAll(l.getVectorRepresentation(resolution));
		}
		return(allPoints);
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}

}
