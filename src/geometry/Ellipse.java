package geometry;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.ArrayList;

import utility.Color;
import utility.PVector;
import utility.Util;

public class Ellipse extends Curve {

	private float cx;
	private float cy;

	public Ellipse(float x, float y, float width, float height) {
		this.cx = x;
		this.cy = y;
		size.x = width;
		size.y = height;

		regenerateHelperVectors(30);
	}

	@Override
	public void render() {
		if (!visible)
			return;
		Color.setGlColor(color);
		glBegin(GL_LINE_LOOP);
		for (PVector v : helperVectors) {
			glVertex2f(v.x, v.y);
		}
		glEnd();
	}

	@Override
	public void regenerateHelperVectors(int resolution) {
		helperVectors = new ArrayList<PVector>();
		for (int i = 0; i < resolution; i++) {
			float p = Util.remap(i,0,resolution,0,Util.TWO_PI);
			PVector v = getVectorOnCurve(p);
			helperVectors.add(v);
		} 
	}

	@Override
	public Point getPointOnCurve(float p) {
		return(new Point(getVectorOnCurve(p)));	
	}
	
	private PVector getVectorOnCurve(float p) {
		PVector v = new PVector((float) (cx + (Math.cos(p) * size.x)), (float) (cy + (Math.sin(p) * size.y)));
		return(v);
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}

}
