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

public class Rect extends Geometry {
	
	private int textureId = -1;

	
	public Rect(double x, double y, double z, double w, double h, double azimuth, double inclination) {
		Vector3d basisX = Util.sphericalToCartesian(w / 2, UtilMath.HALF_PI, azimuth);
		Vector3d basisY = Util.sphericalToCartesian(h / 2, UtilMath.HALF_PI - inclination, azimuth + UtilMath.HALF_PI);

		Vector3d basisZ = basisX.cross(basisY,new Vector3d());
		basisZ.normalize(((w / 2) + (h / 2) / 2));

		// System.out.println("bx : " + basisX);
		// System.out.println("by : " + basisY);

		/* @formatter:off*/
		Matrix4d matrix = new Matrix4d(basisX.x, basisY.x, basisZ.x, x, 
	              					   basisX.y, basisY.y, basisZ.y, y, 
	              					   basisX.z, basisY.z, basisZ.z, z, 
	              					   0,        0,        0,        1).transpose();
		/* @formatter:on*/
		setMatrix(new Matrix4dAnimated(matrix, "Rect"));
		this.colorFill = new Color3i(255, 255, 255);
	}

	
	public Rect(double x, double y, double z, double width, double height) {
		/* @formatter:off*/
		Matrix4d matrix = new Matrix4d(width, 0,      0, x, 
				              		   0,     height, 0, y, 
				              		   0,     0,      1, z,
				              		   0,     0,      0, 1).transpose();
		/* @formatter:on*/
		setMatrix(new Matrix4dAnimated(matrix, "Rect"));
	}
	
	
	public Rect(double x, double y, double width, double height, int textureId) {
		/* @formatter:off*/
		Matrix4d matrix = new Matrix4d(width, 0,      0, x, 
				              		   0,     height, 0, y, 
				              		   0,     0,      1, 0,
				              		   0,     0,      0, 1).transpose();
		/* @formatter:on*/
		setMatrix(new Matrix4dAnimated(matrix, "Rect"));
		
		this.textureId = textureId;
	}



	@Override
	public void render(double time) {
		if (textureId == -1) {
			Graphics3D.drawRect(modelMatrix.get(time), colorFill, -1);
		}
		else {
			Graphics3D.drawRect(modelMatrix.get(time), null, textureId);
		}
	}
	
	
	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		return(null);
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}
}
