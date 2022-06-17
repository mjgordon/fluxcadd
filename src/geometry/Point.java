package geometry;

import java.util.ArrayList;

import graphics.OGLWrapper;
import intersection.Intersection;
import utility.PMatrix3D;
import utility.PVector;
import static org.lwjgl.opengl.GL11.*;

public class Point extends Geometry {

	
	public Point(float x, float y, float z) {
		super();
		
		frame = new PMatrix3D(0,0,0,x,
		                      0,0,0,y,
		                      0,0,0,z,
		                      0,0,0,1);
	}

	public Point(PVector v) {
		super();
		frame = new PMatrix3D(0,0,0,v.x,
                			  0,0,0,v.y,
                			  0,0,0,v.z,
                			  0,0,0,1);
	}

	@Override
	public void render() {
		if (!visible)
			return;
		glPointSize(4);
		if (colorFill != null) {
			OGLWrapper.glColor(colorFill);
			glBegin(GL_POINTS);
			glVertex3f(frame.m03, frame.m13, frame.m23);
			glEnd();
			glPointSize(1);
		}

	}
	
	public float x() {
		return frame.m03;
	}
	
	public float y() {
		return frame.m13;
	}
	
	public float z() {
		return frame.m23;
	}
	
	/**
	 * Gets a copy of the internal vector object
	 * @return
	 */
	public PVector getVector() {
		return new PVector(frame.m03,frame.m13,frame.m23);
	}
	
	public float dist(Point point) {
		float dx = x() - point.x();
		float dy = y() - point.y();
		float dz = z() - point.z();
		
		return((float)Math.sqrt( (dx * dx) + (dy * dy) + (dz * dz)));
	}

	@Override
	public PVector[] getVectorRepresentation(float resolution) {
		PVector[] out = {getVector()};
		return (out);
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>());
	}
	
	@Override
	public void recalculateExplicitGeometry() {
		explicitGeometry = this;
	}

	@Override
	public Intersection intersectLine(PVector start, PVector end) {
		// TODO Auto-generated method stub
		return null;
	}
}
