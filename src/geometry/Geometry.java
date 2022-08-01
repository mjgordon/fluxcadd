package geometry;

import java.util.ArrayList;
import java.util.UUID;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;
import utility.Color;

/**
 * Geometry existing in 2d or 3d space. Can be made of arbitrary structures of
 * other Geometry
 *
 */
public abstract class Geometry {

	public UUID guid;

	public String name;

	public boolean visible = true;

	protected Color colorFill;
	protected Color colorStroke;

	public Matrix4d frame = new Matrix4d();

	private ArrayList<Integer> tags;

	protected Geometry explicitGeometry;


	public Geometry() {
		this.colorFill = new Color(255, 255, 255);
		this.colorStroke = new Color(0, 0, 0);
		tags = new ArrayList<Integer>();
		tags.add(Tag.TAG_DEFAULT);
	}


	public Geometry setColor(int r, int g, int b) {
		this.colorFill.r = r;
		this.colorFill.g = g;
		this.colorFill.b = b;

		return this;
	}


	public Geometry setColor(Color c) {
		setColor(c.r, c.g, c.b);
		return this;
	}


	public Vector3d getPositionVector() {
		return (frame.getColumn(3, new Vector3d()));
	}


	public void renderFrame() {
		Vector3d position = getPositionVector();

		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_LINES);
		OGLWrapper.glVertex(position);
		OGLWrapper.glVertex(frame.getColumn(0, new Vector3d()).add(position));
		GL11.glEnd();

		GL11.glColor3f(0, 1, 0);
		GL11.glBegin(GL11.GL_LINES);
		OGLWrapper.glVertex(position);
		OGLWrapper.glVertex(frame.getColumn(1, new Vector3d()).add(position));
		GL11.glEnd();

		GL11.glColor3f(0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		OGLWrapper.glVertex(position);
		OGLWrapper.glVertex(frame.getColumn(2, new Vector3d()).add(position));
		GL11.glEnd();
	}


	public abstract void render();

	public abstract Intersection intersectLine(Vector3d start, Vector3d end);

	public abstract void recalculateExplicitGeometry();

	public abstract Vector3d[] getVectorRepresentation(double resolution);

	/**
	 * If applicable, returns an ArrayList of Lines representing a hatching fill of
	 * the geometry
	 * 
	 * @return
	 */
	public abstract ArrayList<Line> getHatchLines();
}
