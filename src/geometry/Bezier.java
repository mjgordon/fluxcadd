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
	public void render(double time) {
		// TODO Auto-generated method stub

	}


	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Vector3d getLocalVectorOnCurve(double t, double time) {

		Vector3d mid1 = anchorStart.getVector(time).lerp(controlStart.getVector(time), t, new Vector3d());
		Vector3d mid2 = controlStart.getVector(time).lerp(controlEnd.getVector(time), t, new Vector3d());
		Vector3d mid3 = controlEnd.getVector(time).lerp(anchorEnd.getVector(time), t, new Vector3d());

		Vector3d mid4 = mid1.lerp(mid2, t, new Vector3d());
		Vector3d mid5 = mid2.lerp(mid3, t, new Vector3d());

		return (mid4.lerp(mid5, t, new Vector3d()));
	}


	public void recalculateExplicitGeometry() {
		int resolution = 10;

		/* Commented this out as now the frame should only apply to the final polyline
		getFrame(Double.NaN).transformPosition(anchorStart.getVector(), anchorStartExplicit);
		getFrame(Double.NaN).transformPosition(anchorEnd.getVector(), anchorEndExplicit);

		frame.transformPosition(controlStart.getVector(), controlStartExplicit);
		frame.transformPosition(controlEnd.getVector(), controlEndExplicit);
		*/

		explicitVectors = new Vector3d[resolution + 1];
		for (int i = 0; i <= resolution; i++) {
			float t = (float) i / resolution;
			explicitVectors[i] = getLocalVectorOnCurve(t,0);
		}

		explicitGeometry = new Polyline(explicitVectors);
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}

}
