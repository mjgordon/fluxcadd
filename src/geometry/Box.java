package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import graphics.Graphics3D;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
import utility.Color3i;
import utility.Util;
import utility.math.UtilMath;

public class Box extends Geometry {

	protected Vector3d[] explicitVertices = new Vector3d[8];


	public Box(Matrix4d matrix) {
		setMatrix(new Matrix4dAnimated(matrix, "Box"));
		recalculateExplicitGeometry();
	}


	public Box(double x, double y, double z, double w, double l, double h, double azimuth) {
		Vector3d basisX = Util.sphericalToCartesian(w / 2, UtilMath.HALF_PI, azimuth);
		Vector3d basisY = Util.sphericalToCartesian(l / 2, UtilMath.HALF_PI, azimuth + UtilMath.HALF_PI);

		Vector3d basisZ = basisX.cross(basisY, new Vector3d());
		basisZ.normalize(h / 2);

		/* @formatter:off*/
		Matrix4d base = new Matrix4d(basisX.x, basisY.x, basisZ.x, x, 
				                     basisX.y, basisY.y, basisZ.y, y, 
				                     basisX.z, basisY.z, basisZ.z, z, 
				                     0,        0,        0,        1).transpose();
		/* @formatter:on*/

		setMatrix(new Matrix4dAnimated(base, "Box"));

		recalculateExplicitGeometry();
		this.colorFill = new Color3i(255, 255, 255);
	}


	public Box(double x, double y, double z, double w, double l, double h, double azimuth, double inclination) {
		Vector3d basisX = Util.sphericalToCartesian(w / 2, UtilMath.HALF_PI - inclination, azimuth);
		Vector3d basisY = Util.sphericalToCartesian(l / 2, UtilMath.HALF_PI, azimuth + UtilMath.HALF_PI);

		Vector3d basisZ = basisX.cross(basisY, new Vector3d());
		basisZ.normalize(h / 2);

		/* @formatter:off*/
		Matrix4d base = new Matrix4d(basisX.x, basisY.x, basisZ.x, x, 
				                     basisX.y, basisY.y, basisZ.y, y, 
				                     basisX.z, basisY.z, basisZ.z, z, 
				                     0,        0,        0,        1).transpose();
		/* @formatter:on*/

		setMatrix(new Matrix4dAnimated(base, "Box"));

		recalculateExplicitGeometry();
		this.colorFill = new Color3i(255, 255, 255);
	}


	public void render(double time) {
		if (!visible) {
			return;
		}
		
		Graphics3D.drawBox(modelMatrix.get(time), colorStroke);
		
		this.renderFrame(time);
	}

	// TODO: FEATURE : getPointRepresentation implementation


	@Override
	public Vector3d[] getVectorRepresentation(double resolution) {
		return new Vector3d[0];
	}


	// TODO : FEATURE : getHatchLines implementation
	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>());
	}


	/**
	 * @formatter:off
	 *
	 * Order of vertices
	 * 
	 *    7------6 
	 *    |\     |\ 
	 *    | 4------5  +z
	 *    | |    | | 
	 *    | |    | |
	 * +x 3-|----2 | 
	 *     \|     \| 
	 *   -x 0------1  -z
	 *     
	 *     -y      +y
	 * @formatter: on
	 */
	@Override
	public void recalculateExplicitGeometry() {
		explicitVertices[0] = new Vector3d(-1, -1, -1);
		explicitVertices[1] = new Vector3d(-1, 1, -1);
		explicitVertices[2] = new Vector3d(1, 1, -1);
		explicitVertices[3] = new Vector3d(1, -1, -1);

		explicitVertices[4] = new Vector3d(-1, -1, 1);
		explicitVertices[5] = new Vector3d(-1, 1, 1);
		explicitVertices[6] = new Vector3d(1, 1, 1);
		explicitVertices[7] = new Vector3d(1, -1, 1);

	}


	public double getLongestEdge(double time) {
		Vector3d basisX = modelMatrix.get(time).getColumn(0, new Vector3d());
		Vector3d basisY = modelMatrix.get(time).getColumn(1, new Vector3d());
		Vector3d basisZ = modelMatrix.get(time).getColumn(2, new Vector3d());

		return (Math.max(basisX.length(), Math.max(basisY.length(), basisZ.length())));
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}
}
