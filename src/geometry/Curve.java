package geometry;

import java.util.ArrayList;

import utility.PVector;

/**
 * Abstract class for any 2d or 3d Curve Element
 */
public abstract class Curve extends Geometry {

	protected Point startPoint;
	protected Point endPoint;
	
	public float displayWidth = 1;

	/**
	 * An ArrayList of Vectors that represent points of the curve. Used in
	 * rendering/output. Geometric operations that handle points on the curve should
	 * use Point objects.
	 */
	ArrayList<PVector> helperVectors = new ArrayList<PVector>();

	public Point getStartPoint() {
		return (startPoint);
	}

	public Point getEndPoint() {
		return (endPoint);
	}

	/**
	 * Returns the Point on the curve at parameter p;
	 * @param p
	 * @return
	 */
	public abstract Point getPointOnCurve(float p);

	/** 
	 * Default Curve helperVectors are just of the start and end point
	 * @param resolution
	 */
	public void regenerateHelperVectors(int resolution) {
		ArrayList<PVector> helperVectors = new ArrayList<PVector>();
		helperVectors.add(startPoint.getVector());
		helperVectors.add(endPoint.getVector());
	}

	@Override
	public ArrayList<PVector> getVectorRepresentation(float resolution) {
		return helperVectors;
	}
	
	

}
