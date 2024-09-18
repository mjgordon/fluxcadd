package geometry;

import org.joml.Vector3d;

/**
 * Abstract class for any 2d or 3d Curve Element
 */
public abstract class Curve extends Geometry {
	
	public float displayWidth = 1;

	/**
	 * An ArrayList of Vectors that represent points of the curve. Used in
	 * rendering/output. Geometric operations that handle points on the curve should
	 * use Point objects.
	 */
	protected Vector3d[] explicitVectors;


	/**
	 * Returns the Point on the curve at parameter p;
	 * @param p
	 * @return
	 */
	public Point getPointOnCurve(double t, double time) {
		Vector3d v = getLocalVectorOnCurve(t, time);
		if (v == null) {
			return null;
		}
		else {
			return new Point(v);
		}
	}
	
	public abstract Vector3d getLocalVectorOnCurve(double t, double time);


	@Override
	public Vector3d[] getVectorRepresentation(double resolution) {
		return explicitVectors;
	}
	
	

}
