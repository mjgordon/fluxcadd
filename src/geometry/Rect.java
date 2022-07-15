package geometry;

import org.lwjgl.opengl.GL11;

import intersection.Intersection;
import utility.Color;
import utility.PMatrix3D;

import utility.PVector;
import utility.Util;

public class Rect extends Polyline {
	
	private int textureId = -1;

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
	
	public Rect(float x, float y, float width, float height, int textureId) {
		/* @formatter:off*/
		frame = new PMatrix3D(width, 0,      0, x, 
				              0,     height, 0, y, 
				              0,     0,      1, 0,
				              0,     0,      0, 1);
		/* @formatter:on*/
		closed = true;

		recalculateExplicitGeometry();
		
		this.textureId = textureId;
	}



	@Override
	public void render() {
		// renderFrame();
		if (textureId == -1) {
			super.render();
		}
		else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			GL11.glBegin(GL11.GL_POLYGON);
			GL11.glTexCoord2f(0,0); 
			GL11.glVertex2f(explicitVectors[0].x, explicitVectors[0].y);
			GL11.glTexCoord2f(1,0); 
			GL11.glVertex2f(explicitVectors[1].x, explicitVectors[1].y);
			GL11.glTexCoord2f(1,1); 
			GL11.glVertex2f(explicitVectors[2].x, explicitVectors[2].y);
			GL11.glTexCoord2f(0,1); 
			GL11.glVertex2f(explicitVectors[3].x, explicitVectors[3].y);

			GL11.glEnd();
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
		}
		
	}
	
	@Override
	public Intersection intersectLine(PVector start, PVector end) {
		return(null);
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
