package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import graphics.Graphics3D;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;


public class Point extends Geometry {

	public Point(float x, float y, float z) {
		super();
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(x,y,z,1));
		modelMatrix = new Matrix4dAnimated(base, "Point");
	}


	public Point(Vector3d v) {
		super();
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(v,1));
		modelMatrix = new Matrix4dAnimated(base, "Point");
	}


	@Override
	public void render(double time) {
		if (visible && colorFill != null) {
			Graphics3D.drawPoint(modelMatrix.get(time).getTranslation(new Vector3d()), colorFill);
		}
	}


	public Vector3d getVector(double time) {
		return modelMatrix.get(time).getTranslation(new Vector3d());
	}


	public double dist(Point point, double time) {
		return getVector(time).distance(point.getVector(time));
	}


	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>());
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}
}
