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

	public Box(Matrix4dAnimated matrix) {
		setMatrix(matrix);
		setupVAO();
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

		setupVAO();
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

		setupVAO();
		this.colorFill = new Color3i(255, 255, 255);
	}


	public void render(double time) {
		if (!visible) {
			return;
		}
		
		Graphics3D.drawBox(modelMatrix.get(time), colorStroke);
		
		renderFrame(time);
	}


	// TODO : FEATURE : getHatchLines implementation
	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>());
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
