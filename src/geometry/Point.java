package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;


public class Point extends Geometry {

	public Point(float x, float y, float z) {
		super();
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(x,y,z,1));
		matrix = new Matrix4dAnimated(base, "Point");
	}


	public Point(Vector3d v) {
		super();
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(v,1));
		matrix = new Matrix4dAnimated(base, "Point");
	}


	@Override
	public void render(double time) {
		if (visible && colorFill != null) {
			GL11.glPointSize(4);
			OGLWrapper.glColor(colorFill);
			
			GL11.glPushMatrix();
			GL11.glMultMatrixd(matrix.getArray(time));
			GL11.glBegin(GL11.GL_POINTS);
			GL11.glVertex3d(0,0,0);
			GL11.glEnd();
			GL11.glPopMatrix();
			
			GL11.glPointSize(1);
		}
	}


	public Vector3d getVector(double time) {
		return matrix.get(time).getColumn(3,new Vector3d());
	}


	public double dist(Point point, double time) {
		return getVector(time).distance(point.getVector(time));
	}


	@Override
	public Vector3d[] getVectorRepresentation(double resolution) {
		Vector3d[] out = { getVector(0) };
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
