package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import graphics.Graphics;
import graphics.Graphics3D;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;

/**
 * Four-point bezier curve segment
 * 
 *
 */
public class Bezier extends Curve {

	private Point anchorStart = null;
	private Point anchorEnd = null;

	private Point controlStart = null;
	private Point controlEnd = null;
	
	private int resolution = 20;


	public Bezier(Vector3d start, Vector3d end, Vector3d controlStart, Vector3d controlEnd) {
		
		setMatrix(new Matrix4dAnimated(new Matrix4d(), "Bezier"));
		
		this.anchorStart = new Point(start);
		this.anchorEnd = new Point(end);
		this.controlStart = new Point(controlStart);
		this.controlEnd = new Point(controlEnd);
		setupVAO();
	}


	public Bezier(Point start, Point end, Point controlStart, Point controlEnd) {
		
		setMatrix(new Matrix4dAnimated(new Matrix4d(), "Bezier"));
		
		this.anchorStart = start;
		this.anchorEnd = end;
		this.controlStart = controlStart;
		this.controlEnd = controlEnd;

		setupVAO();
	}


	@Override
	public void render(double time) {
		if (!visible) {
			return;
		}
				
		Graphics3D.drawPolyLine(glidVAO, resolution, modelMatrix.get(time), colorStroke);
	}


	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}


	
	@Override
	public Vector3d getLocalVectorOnCurve(double t, double time) {
		// TODO: See if we can reduce allocation
		Vector3d mid1 = anchorStart.getVector(time).lerp(controlStart.getVector(time), t, new Vector3d());
		Vector3d mid2 = controlStart.getVector(time).lerp(controlEnd.getVector(time), t, new Vector3d());
		Vector3d mid3 = controlEnd.getVector(time).lerp(anchorEnd.getVector(time), t, new Vector3d());

		Vector3d mid4 = mid1.lerp(mid2, t, new Vector3d());
		Vector3d mid5 = mid2.lerp(mid3, t, new Vector3d());

		return (mid4.lerp(mid5, t, new Vector3d()));
	}


	@SuppressWarnings("static-access")
	@Override
	public void setupVAO() {
		super.setupVAO();
		
		float[] vertices = new float[resolution * 3];
		
		for (int i = 0; i < resolution; i++) {
			float t = (float) i / (resolution - 1);
			
			Vector3d v = getLocalVectorOnCurve(t,0);
			
			vertices[i * 3 + 0] = (float)v.x;
			vertices[i * 3 + 1] = (float)v.y;
			vertices[i * 3 + 2] = (float)v.z;
		}
		
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			glidVBO = Graphics.initVBO(stack, vertices);
			
			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
		}
		
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
		GL33.glBindVertexArray(0);
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}

}
