package geometry;

import java.util.ArrayList;

import utility.Color;
import utility.PMatrix3D;

import utility.PVector;
import utility.Util;

public class Rect extends Polyline {

	public Rect(float x, float y, float z, float w, float h, float azimuth, float inclination) {
		PVector basisX = Util.sphereToCart(w / 2, Util.HALF_PI, azimuth);
		PVector basisY = Util.sphereToCart(h / 2, Util.HALF_PI - inclination, azimuth + Util.HALF_PI);

		PVector basisZ = basisX.cross(basisY);
		basisZ.setMag(((w / 2) + (h / 2) / 2));

		// System.out.println("bx : " + basisX);
		// System.out.println("by : " + basisY);

		/* @formatter:off*/
		frame = new PMatrix3D(basisX.x, basisY.x, basisZ.x, x, 
				              basisX.y, basisY.y, basisZ.y, y, 
				              basisX.z, basisY.z, basisZ.z, z, 
				              0,        0,        0,        1);
		/* @formatter:on*/

		closed = true;
		recalculateExplicitGeometry();
		this.colorFill = new Color(255, 255, 255);

	}

	public Rect(float x, float y, float z, float width, float height) {
		/* @formatter:off*/
		frame = new PMatrix3D(width, 0,      0, x, 
				              0,     height, 0, y, 
				              0,     0,      1, z,
				              0,     0,      0, 1);
		/* @formatter:on*/
		closed = true;

		recalculateExplicitGeometry();
	}



	@Override
	public void render() {
		// renderFrame();
		super.render();
	}

	@Override
	public void recalculateExplicitGeometry() {
		explicitVectors = new PVector[4];
		
		explicitVectors[0] = frame.mult(new PVector(-1, -1, 0), null);
		explicitVectors[1] = frame.mult(new PVector(1, -1, 0), null);
		explicitVectors[2] = frame.mult(new PVector(1, 1, 0), null);
		explicitVectors[3] = frame.mult(new PVector(-1, 1, 0), null);
	}
}
