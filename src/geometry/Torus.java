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
import utility.Color3i;

public class Torus extends Geometry {
	
	private double ringRadius;
	private double profileRadius;
	
	public Torus(Matrix4dAnimated modelMatrixInput, Color3i colorFill, double ringRadius, double profileRadius) {
		
		this.modelMatrix = new Matrix4dAnimated("Sphere");
		
		for (double timeStamp : modelMatrixInput.getKeyframes()) {
			this.modelMatrix.addKeyframe(timeStamp, (new Matrix4d(modelMatrixInput.get(timeStamp))));
		}
		
		this.colorFill = colorFill;
		
		this.ringRadius = ringRadius;
		this.profileRadius = profileRadius;
		
		setupVAO();
	}

	@Override
	public void render(double time) {
		Graphics3D.drawTorus(glidVAO, modelMatrix.get(time), colorFill);
	}

	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@SuppressWarnings("static-access")
	@Override
	public void setupVAO() {
		super.setupVAO();
		
		// Major rings are long (e.g. number of vertices of the profile), minor rings are short (e.g. number of vertices of the main circle)
		int minorCount = 16;
		int majorCount = 8;
		
		float[] verticesTorus = new float[majorCount * minorCount * 3];
		
		for (int i = 0; i < minorCount; i++) {
			double angMinor = Math.PI * 2 * i / minorCount;

			for (int j = 0; j < majorCount; j++) {
				double angMajor = Math.PI * 2 * j / majorCount;
				Vector3d a = new Vector3d(Math.cos(angMajor) * profileRadius, Math.sin(angMajor) * profileRadius, 0);
				a.rotateX(Math.PI / 2);
				a.x += ringRadius;
				a.rotateZ(angMinor);
				
				verticesTorus[(i * majorCount + j) * 3 + 0] = (float)a.x;
				verticesTorus[(i * majorCount + j) * 3 + 1] = (float)a.y;
				verticesTorus[(i * majorCount + j) * 3 + 2] = (float)a.z;
			}
		} 
		
		int[] indices = new int[majorCount * minorCount * 4 ];
		for (int i = 0; i < minorCount; i++) {
			for (int j = 0; j < majorCount; j++) {
				indices[(i * majorCount + j) * 2 + 0] = i * majorCount + j;
				indices[(i * majorCount + j) * 2 + 1] = i * majorCount + ((j + 1) % majorCount);
			}
		} 
		
		int halfOffset = indices.length / 2;
		
		for (int j = 0; j < majorCount; j++) {
			for (int i = 0; i < minorCount; i++) {
				indices[(j * minorCount + i) * 2 + 0 + halfOffset] = i * majorCount + j;
				indices[(j * minorCount + i) * 2 + 1 + halfOffset] = ((i + 1) % minorCount) * majorCount + j;
			}
		} 
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			glidVBO = Graphics.initVBO(stack, verticesTorus);
			glidEBO = Graphics.initEBO(stack, indices);

			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
		}
	}
}
