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
import utility.math.UtilMath;

// TODO : Implement animation
public class Ellipse extends Curve {

	public Ellipse(float x, float y, float width, float height) {
		/* @formatter:off*/
		setFrame(new Matrix4d(width, 0,      0, x, 
				              0,     height, 0, y, 
				              0,     0,      1, 0, 
				              0,     0,      0, 1));
		/* @formatter:on*/

		recalculateExplicitGeometry();
	}


	@Override
	public void render(double time) {
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
			double t = UtilMath.map(i, 0, resolution, 0, UtilMath.TWO_PI);
			explicitVectors[i] = getLocalVectorOnCurve(t, 0);
		}
	}


	public Vector3d getLocalVectorOnCurve(double t, double time) {
		Vector3d v = new Vector3d((frame.get(time).m03() + (Math.cos(t) * frame.get(time).m00())), (frame.get(time).m13() + (Math.sin(t) * frame.get(time).m11())), 0F);
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
