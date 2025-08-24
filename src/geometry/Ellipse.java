package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import graphics.Graphics3D;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
import utility.math.UtilMath;


public class Ellipse extends Curve {

	public Ellipse(float x, float y, float width, float height) {
		/* @formatter:off*/
		Matrix4d matrix = new Matrix4d(width, 0,      0, x, 
				                       0,     height, 0, y, 
				                       0,     0,      1, 0, 
				                       0,     0,      0, 1).transpose();
		/* @formatter:on*/
		setMatrix(new Matrix4dAnimated(matrix, "Ellipse"));

		recalculateExplicitGeometry();
	}


	@Override
	public void render(double time) {
		if (!visible) {
			return;
		}
		
		Graphics3D.drawEllipse(modelMatrix.get(time), colorStroke);
	}


	@Override
	public void recalculateExplicitGeometry() {
		int resolution = 32;
		explicitVectors = new Vector3d[resolution];
		for (int i = 0; i < resolution; i++) {
			double t = UtilMath.map(i, 0, resolution, 0, UtilMath.TWO_PI);
			explicitVectors[i] = getLocalVectorOnCurve(t, 0);
		}
	}


	@Override
	public Vector3d getLocalVectorOnCurve(double t, double time) {
		Vector3d v = new Vector3d(Math.cos(t), Math.sin(t), 0);
		
		v.mulPosition(modelMatrix.get(time));
		
		return v;
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
