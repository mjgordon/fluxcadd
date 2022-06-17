package geometry;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.util.ArrayList;

import graphics.OGLWrapper;
import intersection.Intersection;
import utility.PMatrix3D;
import utility.PVector;
import utility.Util;

public class Ellipse extends Curve {
	

	public Ellipse(float x, float y, float width, float height) {
		frame = new PMatrix3D(width,0,     0,x,
							  0,    height,0,y,
							  0,    0,     1,0,
							  0,    0,     0,1);
		
		recalculateExplicitGeometry();
	}

	@Override
	public void render() {
		if (!visible)
			return;
		OGLWrapper.glColor(colorFill);
		
		glBegin(GL_LINE_LOOP);
		for (PVector v : explicitVectors) {
			glVertex2f(v.x, v.y);
		}
		glEnd();
	}

	@Override
	public void recalculateExplicitGeometry() {
		int resolution = 10;
		explicitVectors = new PVector[resolution];
		for (int i = 0; i < resolution; i++) {
			float p = Util.remap(i,0,resolution,0,Util.TWO_PI);
			explicitVectors[i] = getVectorOnCurve(p);
		} 
	}

	
	public PVector getVectorOnCurve(float p) {
		PVector v = new PVector((float) (frame.m03 + (Math.cos(p) * frame.m00)), (float) (frame.m13 + (Math.sin(p) * frame.m11)));
		return(v);
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Intersection intersectLine(PVector start, PVector end) {
		// TODO Auto-generated method stub
		return null;
	}

}
