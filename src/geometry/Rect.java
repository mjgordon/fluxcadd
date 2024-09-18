package geometry;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import intersection.Intersection;
import utility.Color;

import utility.Util;
import utility.math.UtilMath;

public class Rect extends Polyline {
	
	private int textureId = -1;

	public Rect(double x, double y, double z, double w, double h, double azimuth, double inclination) {
		Vector3d basisX = Util.sphericalToCartesian(w / 2, UtilMath.HALF_PI, azimuth);
		Vector3d basisY = Util.sphericalToCartesian(h / 2, UtilMath.HALF_PI - inclination, azimuth + UtilMath.HALF_PI);

		Vector3d basisZ = basisX.cross(basisY,new Vector3d());
		basisZ.normalize(((w / 2) + (h / 2) / 2));

		// System.out.println("bx : " + basisX);
		// System.out.println("by : " + basisY);

		/* @formatter:off*/
		setFrame(new Matrix4d(basisX.x, basisY.x, basisZ.x, x, 
				              basisX.y, basisY.y, basisZ.y, y, 
				              basisX.z, basisY.z, basisZ.z, z, 
				              0,        0,        0,        1).transpose());
		/* @formatter:on*/

		closed = true;
		recalculateExplicitGeometry();
		this.colorFill = new Color(255, 255, 255);

	}

	public Rect(double x, double y, double z, double width, double height) {
		/* @formatter:off*/
		setFrame(new Matrix4d(width, 0,      0, x, 
				              0,     height, 0, y, 
				              0,     0,      1, z,
				              0,     0,      0, 1).transpose());
		/* @formatter:on*/
		closed = true;

		recalculateExplicitGeometry();
	}
	
	public Rect(double x, double y, double width, double height, int textureId) {
		/* @formatter:off*/
		setFrame(new Matrix4d(width, 0,      0, x, 
				              0,     height, 0, y, 
				              0,     0,      1, 0,
 				              0,     0,      0, 1).transpose());
		/* @formatter:on*/
		closed = true;

		recalculateExplicitGeometry();
		
		this.textureId = textureId;
	}



	@Override
	public void render(double time) {
		// renderFrame();
		if (textureId == -1) {
			super.render(time);
		}
		else {
			GL11.glPushMatrix();
			GL11.glMultMatrixd(frame.getArray(time));
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			GL11.glBegin(GL11.GL_POLYGON);
			GL11.glTexCoord2d(0,0); 
			GL11.glVertex2d(explicitVectors[0].x, explicitVectors[0].y);
			GL11.glTexCoord2d(1,0); 
			GL11.glVertex2d(explicitVectors[1].x, explicitVectors[1].y);
			GL11.glTexCoord2d(1,1); 
			GL11.glVertex2d(explicitVectors[2].x, explicitVectors[2].y);
			GL11.glTexCoord2d(0,1); 
			GL11.glVertex2d(explicitVectors[3].x, explicitVectors[3].y);

			GL11.glEnd();
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			
			GL11.glPopMatrix();
		}
		
	}
	
	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		return(null);
	}

	@Override
	public void recalculateExplicitGeometry() {
		explicitVectors = new Vector3d[4];
		
		explicitVectors[0] = new Vector3d(-1, -1, 0);
		explicitVectors[1] = new Vector3d(1, -1, 0);
		explicitVectors[2] = new Vector3d(1, 1, 0);
		explicitVectors[3] = new Vector3d(-1, 1, 0);
	}
}
