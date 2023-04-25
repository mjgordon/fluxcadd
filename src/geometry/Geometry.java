package geometry;

import java.util.ArrayList;
import java.util.UUID;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
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
	
	public Matrix4dAnimated frame;

	private ArrayList<Integer> tags;

	protected Geometry explicitGeometry;


	public Geometry() {
		this.colorFill = new Color(255, 255, 255);
		this.colorStroke = new Color(0, 0, 0);
		tags = new ArrayList<Integer>();
		tags.add(Tag.TAG_DEFAULT);
	}


	public Geometry setFillColor(int r, int g, int b) {
		this.colorFill.r = r;
		this.colorFill.g = g;
		this.colorFill.b = b;

		return this;
	}


	public Geometry setFillColor(Color c) {
		setFillColor(c.r, c.g, c.b);
		return this;
	}


	public Geometry clearFillColor() {
		this.colorFill = null;

		return this;
	}




	public void renderFrame(double time) {
		Vector3d position = frame.get(time).getColumn(3, new Vector3d());

		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_LINES);
		OGLWrapper.glVertex(position);
		OGLWrapper.glVertex(frame.get(time).getColumn(0, new Vector3d()).add(position));
		GL11.glEnd();

		GL11.glColor3f(0, 1, 0);
		GL11.glBegin(GL11.GL_LINES);
		OGLWrapper.glVertex(position);
		OGLWrapper.glVertex(frame.get(time).getColumn(1, new Vector3d()).add(position));
		GL11.glEnd();

		GL11.glColor3f(0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		OGLWrapper.glVertex(position);
		OGLWrapper.glVertex(frame.get(time).getColumn(2, new Vector3d()).add(position));
		GL11.glEnd();
	}


	public abstract void render(double time);
	
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

	
	public void setFrame(Matrix4dAnimated frame) {
		this.frame = frame;
	}
	
	@Deprecated
	public void setFrame(Matrix4d m4d) {
		this.frame = new Matrix4dAnimated(m4d, "Geometry");
	}
}
