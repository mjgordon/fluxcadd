package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import graphics.OGLWrapper;
import intersection.Intersection;
import static org.lwjgl.opengl.GL11.*;

public class Point extends Geometry {

	public Point(float x, float y, float z) {
		super();
		frame = new Matrix4d().setColumn(3, new Vector4d(x,y,z,1));
	}


	public Point(Vector3d v) {
		super();
		frame = new Matrix4d().setColumn(3, new Vector4d(v,1));
	}


	@Override
	public void render() {
		if (!visible)
			return;
		glPointSize(4);
		if (colorFill != null) {
			OGLWrapper.glColor(colorFill);
			glBegin(GL_POINTS);
			glVertex3d(x(), y(), z());
			glEnd();
			glPointSize(1);
		}

	}


	public double x() {
		return frame.m30();
	}


	public double y() {
		return frame.m31();
	}


	public double z() {
		return frame.m32();
	}


	/**
	 * Gets a copy of the internal vector object
	 * 
	 * @return
	 */
	public Vector3d getVector() {
		return new Vector3d(x(), y(), z());
	}


	public double dist(Point point) {
		double dx = x() - point.x();
		double dy = y() - point.y();
		double dz = z() - point.z();

		return (Math.sqrt((dx * dx) + (dy * dy) + (dz * dz)));
	}


	@Override
	public Vector3d[] getVectorRepresentation(double resolution) {
		Vector3d[] out = { getVector() };
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
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}
}
