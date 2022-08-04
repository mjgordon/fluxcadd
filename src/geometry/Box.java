package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;
import utility.Color;
import utility.Util;
import utility.math.UtilMath;

public class Box extends Geometry {

	protected Vector3d[] explicitVertices = new Vector3d[8];


	public Box(Matrix4d frame) {
		this.frame = frame;
		recalculateExplicitGeometry();
	}


	public Box(double x, double y, double z, double w, double l, double h, double azimuth) {
		Vector3d basisX = Util.sphericalToCartesian(w / 2, UtilMath.HALF_PI, azimuth);
		Vector3d basisY = Util.sphericalToCartesian(l / 2, UtilMath.HALF_PI, azimuth + UtilMath.HALF_PI);

		Vector3d basisZ = basisX.cross(basisY);
		basisZ.normalize(h / 2);

		System.out.println(w + " : " + l + " : " + h);

		/* @formatter:off*/
		frame = new Matrix4d(basisX.x, basisY.x, basisZ.x, x, 
				             basisX.y, basisY.y, basisZ.y, y, 
				             basisX.z, basisY.z, basisZ.z, z, 
				             0,        0,        0,        1).transpose();
		/* @formatter:on*/

		recalculateExplicitGeometry();
		this.colorFill = new Color(255, 255, 255);
	}


	public Box(double x, double y, double z, double w, double l, double h, double azimuth, double inclination) {
		Vector3d basisX = Util.sphericalToCartesian(w / 2, UtilMath.HALF_PI - inclination, azimuth);
		Vector3d basisY = Util.sphericalToCartesian(l / 2, UtilMath.HALF_PI, azimuth + UtilMath.HALF_PI);

		Vector3d basisZ = basisX.cross(basisY);
		basisZ.normalize(h / 2);

		/* @formatter:off*/
		frame = new Matrix4d(basisX.x, basisY.x, basisZ.x, x, 
				             basisX.y, basisY.y, basisZ.y, y, 
				             basisX.z, basisY.z, basisZ.z, z, 
				             0,        0,        0,        1).transpose();
		/* @formatter:on*/

		recalculateExplicitGeometry();
		this.colorFill = new Color(255, 255, 255);
	}


	public void render() {
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
		explicitVertices[0] = frame.transformPosition(new Vector3d(-1, -1, -1));
		explicitVertices[1] = frame.transformPosition(new Vector3d(-1, 1, -1));
		explicitVertices[2] = frame.transformPosition(new Vector3d(1, 1, -1));
		explicitVertices[3] = frame.transformPosition(new Vector3d(1, -1, -1));

		explicitVertices[4] = frame.transformPosition(new Vector3d(-1, -1, 1));
		explicitVertices[5] = frame.transformPosition(new Vector3d(-1, 1, 1));
		explicitVertices[6] = frame.transformPosition(new Vector3d(1, 1, 1));
		explicitVertices[7] = frame.transformPosition(new Vector3d(1, -1, 1));

	}


	public double getLongestEdge() {
		Vector3d basisX = frame.getColumn(0, new Vector3d());
		Vector3d basisY = frame.getColumn(1, new Vector3d());
		Vector3d basisZ = frame.getColumn(2, new Vector3d());

		return (Math.max(basisX.length(), Math.max(basisY.length(), basisZ.length())));
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}
}
