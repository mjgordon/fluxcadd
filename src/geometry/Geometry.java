package geometry;

import java.util.ArrayList;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import utility.PVector;
import utility.Color;
import utility.PMatrix3D;

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

	public PMatrix3D frame = new PMatrix3D();

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

	public PVector getPositionVector() {
		return (frame.getPositionVector());
	}

	public void renderFrame() {
		PVector position = frame.getPositionVector();

		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_LINES);
		OGLWrapper.glVertex(position);
		OGLWrapper.glVertex(PVector.add(position, frame.getXBasis()));
		GL11.glEnd();

		GL11.glColor3f(0, 1, 0);
		GL11.glBegin(GL11.GL_LINES);
		OGLWrapper.glVertex(position);
		OGLWrapper.glVertex(PVector.add(position, frame.getYBasis()));
		GL11.glEnd();

		GL11.glColor3f(0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		OGLWrapper.glVertex(position);
		OGLWrapper.glVertex(PVector.add(position, frame.getZBasis()));
		GL11.glEnd();
	}

	public abstract void render();

	public abstract void recalculateExplicitGeometry();

	public abstract PVector[] getVectorRepresentation(float resolution);

	/**
	 * If applicable, returns an ArrayList of Lines representing a hatching fill of
	 * the geometry
	 * 
	 * @return
	 */
	public abstract ArrayList<Line> getHatchLines();

}
