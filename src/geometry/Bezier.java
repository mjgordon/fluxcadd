package geometry;

import java.util.ArrayList;

import org.joml.Vector3d;

import intersection.Intersection;

/**
 * Four-point bezier curve segment
 * 
 *
 */
public class Bezier extends Curve {

	private Point anchorStart = null;
	private Point anchorEnd = null;

	private Point controlStart = null;
	private Point controlEnd = null;

	private Vector3d anchorStartExplicit;
	private Vector3d anchorEndExplicit;
	private Vector3d controlStartExplicit;
	private Vector3d controlEndExplicit;


	public Bezier(Vector3d start, Vector3d end, Vector3d controlStart, Vector3d controlEnd) {
		this.anchorStart = new Point(start);
		this.anchorEnd = new Point(end);
		this.controlStart = new Point(controlStart);
		this.controlEnd = new Point(controlEnd);
		recalculateExplicitGeometry();
	}


	public Bezier(Point start, Point end, Point controlStart, Point controlEnd) {
		this.anchorStart = start;
		this.anchorEnd = end;
		this.controlStart = controlStart;
		this.controlEnd = controlEnd;

		recalculateExplicitGeometry();
	}


	@Override
	public void render() {
		// TODO Auto-generated method stub

	}


	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Vector3d getVectorOnCurve(double t) {

		Vector3d mid1 = anchorStartExplicit.lerp(controlStartExplicit, t, new Vector3d());
		Vector3d mid2 = controlStartExplicit.lerp(controlEndExplicit, t, new Vector3d());
		Vector3d mid3 = controlEndExplicit.lerp(anchorEndExplicit, t, new Vector3d());

		Vector3d mid4 = mid1.lerp(mid2, t, new Vector3d());
		Vector3d mid5 = mid2.lerp(mid3, t, new Vector3d());

		return (mid4.lerp(mid5, t, new Vector3d()));
	}


	public void recalculateExplicitGeometry() {
		int resolution = 10;

		frame.transformPosition(anchorStart.getVector(), anchorStartExplicit);
		frame.transformPosition(anchorEnd.getVector(), anchorEndExplicit);

		frame.transformPosition(controlStart.getVector(), controlStartExplicit);
		frame.transformPosition(controlEnd.getVector(), controlEndExplicit);

		explicitVectors = new Vector3d[resolution + 1];
		for (int i = 0; i <= resolution; i++) {
			float t = (float) i / resolution;
			explicitVectors[i] = getVectorOnCurve(t);
		}

		explicitGeometry = new Polyline(explicitVectors);

	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}

}
