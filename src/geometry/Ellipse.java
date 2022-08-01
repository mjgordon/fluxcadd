package geometry;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2d;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import graphics.OGLWrapper;
import intersection.Intersection;
import utility.Util;

public class Ellipse extends Curve {

	public Ellipse(float x, float y, float width, float height) {
		frame = new Matrix4d(width, 0, 0, x, 0, height, 0, y, 0, 0, 1, 0, 0, 0, 0, 1);

		recalculateExplicitGeometry();
	}


	@Override
	public void render() {
		if (!visible)
			return;
		OGLWrapper.glColor(colorFill);

		glBegin(GL_LINE_LOOP);
		for (Vector3d v : explicitVectors) {
			glVertex2d(v.x, v.y);
		}
		glEnd();
	}


	@Override
	public void recalculateExplicitGeometry() {
		int resolution = 10;
		explicitVectors = new Vector3d[resolution];
		for (int i = 0; i < resolution; i++) {
			double p = Util.map(i, 0, resolution, 0, Util.TWO_PI);
			explicitVectors[i] = getVectorOnCurve(p);
		}
	}


	public Vector3d getVectorOnCurve(double p) {
		Vector3d v = new Vector3d((frame.m03() + (Math.cos(p) * frame.m00())), (frame.m13() + (Math.sin(p) * frame.m11())), 0F);
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
