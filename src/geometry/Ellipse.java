package geometry;


import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import graphics.OGLWrapper;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
import utility.math.UtilMath;


public class Ellipse extends Curve {

	public Ellipse(float x, float y, float width, float height) {
		/* @formatter:off*/
		Matrix4d matrix = new Matrix4d(width, 0,      0, x, 
				                       0,     height, 0, y, 
				                       0,     0,      1, 0, 
				                       0,     0,      0, 1);
		/* @formatter:on*/
		setMatrix(new Matrix4dAnimated(matrix, "Ellipse"));

		recalculateExplicitGeometry();
	}


	@Override
	public void render(double time) {
		if (!visible)
			return;
		OGLWrapper.glColor(colorFill);

		GL11.glBegin(GL11.GL_LINE_LOOP);
		for (Vector3d v : explicitVectors) {
			GL11.glVertex2d(v.x, v.y);
		}
		GL11.glEnd();
	}


	@Override
	public void recalculateExplicitGeometry() {
		int resolution = 10;
		explicitVectors = new Vector3d[resolution];
		for (int i = 0; i < resolution; i++) {
			double t = UtilMath.map(i, 0, resolution, 0, UtilMath.TWO_PI);
			explicitVectors[i] = getLocalVectorOnCurve(t, 0);
		}
	}


	@Override
	public Vector3d getLocalVectorOnCurve(double t, double time) {
		Vector3d v = new Vector3d((matrix.get(time).m03() + (Math.cos(t) * matrix.get(time).m00())), (matrix.get(time).m13() + (Math.sin(t) * matrix.get(time).m11())), 0F);
		return (v);
	}


	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}

}
