package geometry;

import java.util.ArrayList;
import java.util.UUID;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL33;

import graphics.Graphics3D;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
import utility.Color3i;

/**
 * Geometry existing in 2d or 3d space. Can be made of arbitrary structures of
 * other Geometry
 */
public abstract class Geometry {

	public UUID guid;

	public String name;

	public boolean visible = true;

	protected Color3i colorFill;
	protected Color3i colorStroke;

	public Matrix4dAnimated modelMatrix;

	private ArrayList<Integer> tags;
	
	public int glidVAO = 0;
	public int glidVBO = 0;
	public int glidEBO = 0;

	public Geometry() {
		this.colorFill = new Color3i(255, 255, 255);
		this.colorStroke = new Color3i(0, 0, 0);
		tags = new ArrayList<Integer>();
		tags.add(Tag.TAG_DEFAULT);
	}



	public Geometry setFillColor(Color3i c) {
		this.colorFill.r = c.r;
		this.colorFill.g = c.g;
		this.colorFill.b = c.b;
		
		return this;
	}


	public Geometry clearFillColor() {
		this.colorFill = null;

		return this;
	}


	public void renderFrame(double time) {
		Graphics3D.drawAxes(modelMatrix.get(time));
	}


	public abstract void render(double time);

	
	public abstract Intersection intersectLine(Vector3d start, Vector3d end);


	/**
	 * Override if the geometry is not representable by a transformed primiive
	 */
	public void setupVAO() {
	}

	
	/**
	 * If applicable, returns an ArrayList of Lines representing a hatching fill of
	 * the geometry
	 * 
	 * @return
	 */
	public abstract ArrayList<Line> getHatchLines();


	public void setMatrix(Matrix4dAnimated matrix) {
		this.modelMatrix = matrix;
	}
	
	/**
	 * Deletes any OpenGL data associated with the element
	 */
	@SuppressWarnings("static-access")
	public void cleanup() {
		if (glidVBO != 0) {
			GL33.glDeleteBuffers(glidVBO);	
		}
		
		if (glidEBO != 0) {
			GL33.glDeleteBuffers(glidEBO);
		}
		
		if (glidVAO != 0) {
			GL33.glDeleteVertexArrays(glidVAO);	
		}
		
		
	}
}
