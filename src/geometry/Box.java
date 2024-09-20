package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
import utility.Color;
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

		Vector3d basisZ = basisX.cross(basisY);
		basisZ.normalize(h / 2);

		/* @formatter:off*/
		Matrix4d base = new Matrix4d(basisX.x, basisY.x, basisZ.x, x, 
				                     basisX.y, basisY.y, basisZ.y, y, 
				                     basisX.z, basisY.z, basisZ.z, z, 
				                     0,        0,        0,        1).transpose();
		/* @formatter:on*/

		setMatrix(new Matrix4dAnimated(base, "Box"));

		recalculateExplicitGeometry();
		this.colorFill = new Color(255, 255, 255);
	}


	public Box(double x, double y, double z, double w, double l, double h, double azimuth, double inclination) {
		Vector3d basisX = Util.sphericalToCartesian(w / 2, UtilMath.HALF_PI - inclination, azimuth);
		Vector3d basisY = Util.sphericalToCartesian(l / 2, UtilMath.HALF_PI, azimuth + UtilMath.HALF_PI);

		Vector3d basisZ = basisX.cross(basisY);
		basisZ.normalize(h / 2);

		/* @formatter:off*/
		Matrix4d base = new Matrix4d(basisX.x, basisY.x, basisZ.x, x, 
				                     basisX.y, basisY.y, basisZ.y, y, 
				                     basisX.z, basisY.z, basisZ.z, z, 
				                     0,        0,        0,        1).transpose();
		/* @formatter:on*/

		setMatrix(new Matrix4dAnimated(base, "Box"));

		recalculateExplicitGeometry();
		this.colorFill = new Color(255, 255, 255);
	}


	public void render(double time) {
		GL11.glPushMatrix();
		{
			GL11.glMultMatrixd(matrix.getArray(time));

			if (!visible) {
				return;
			}

			if (colorFill != null) {
				OGLWrapper.glColor(colorFill);

				GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
				OGLWrapper.glVertex(explicitVertices[0]);
				OGLWrapper.glVertex(explicitVertices[4]);
				OGLWrapper.glVertex(explicitVertices[1]);
				OGLWrapper.glVertex(explicitVertices[5]);
				OGLWrapper.glVertex(explicitVertices[2]);
				OGLWrapper.glVertex(explicitVertices[6]);
				OGLWrapper.glVertex(explicitVertices[3]);
				OGLWrapper.glVertex(explicitVertices[7]);
				OGLWrapper.glVertex(explicitVertices[0]);
				OGLWrapper.glVertex(explicitVertices[4]);
				GL11.glEnd();

				GL11.glBegin(GL11.GL_QUADS);
				OGLWrapper.glVertex(explicitVertices[0]);
				OGLWrapper.glVertex(explicitVertices[1]);
				OGLWrapper.glVertex(explicitVertices[2]);
				OGLWrapper.glVertex(explicitVertices[3]);

				OGLWrapper.glVertex(explicitVertices[4]);
				OGLWrapper.glVertex(explicitVertices[5]);
				OGLWrapper.glVertex(explicitVertices[6]);
				OGLWrapper.glVertex(explicitVertices[7]);

				GL11.glEnd();
			}

			GL11.glColor3f(0, 0, 0);

			// Upper Horizontals
			GL11.glBegin(GL11.GL_LINE_LOOP);

			OGLWrapper.glVertex(explicitVertices[0]);
			OGLWrapper.glVertex(explicitVertices[1]);
			OGLWrapper.glVertex(explicitVertices[2]);
			OGLWrapper.glVertex(explicitVertices[3]);

			GL11.glEnd();

			// Lower Horizontals
			GL11.glBegin(GL11.GL_LINE_LOOP);

			OGLWrapper.glVertex(explicitVertices[4]);
			OGLWrapper.glVertex(explicitVertices[5]);
			OGLWrapper.glVertex(explicitVertices[6]);
			OGLWrapper.glVertex(explicitVertices[7]);

			GL11.glEnd();

			// Verticals
			GL11.glBegin(GL11.GL_LINES);

			OGLWrapper.glVertex(explicitVertices[0]);
			OGLWrapper.glVertex(explicitVertices[4]);

			OGLWrapper.glVertex(explicitVertices[1]);
			OGLWrapper.glVertex(explicitVertices[5]);

			OGLWrapper.glVertex(explicitVertices[2]);
			OGLWrapper.glVertex(explicitVertices[6]);

			OGLWrapper.glVertex(explicitVertices[3]);
			OGLWrapper.glVertex(explicitVertices[7]);

			GL11.glEnd();
		}
		GL11.glPopMatrix();
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


	@Override
	/**
	 * Order of vertices
	 * 
	 * 7------6 |\ |\ | 4----|-5 +X | | | | \ 3------2 | -Y-.-+Y \| \| \ 0------1 -X
	 * 
	 * 
	 */
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
		Vector3d basisX = matrix.get(time).getColumn(0, new Vector3d());
		Vector3d basisY = matrix.get(time).getColumn(1, new Vector3d());
		Vector3d basisZ = matrix.get(time).getColumn(2, new Vector3d());

		return (Math.max(basisX.length(), Math.max(basisY.length(), basisZ.length())));
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}
}
