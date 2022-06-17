package geometry;

import utility.PVector;

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
	protected PVector[] explicitVectors;


	/**
	 * Returns the Point on the curve at parameter p;
	 * @param p
	 * @return
	 */
	public Point getPointOnCurve(float p) {
		PVector v = getVectorOnCurve(p);
		if (v == null) {
			return null;
		}
		else {
			return new Point(v);
		}
	}
	
	public abstract PVector getVectorOnCurve(float p);


	@Override
	public PVector[] getVectorRepresentation(float resolution) {
		return explicitVectors;
	}
	
	

}
