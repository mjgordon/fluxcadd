package geometry;

import java.util.ArrayList;
import java.util.Arrays;

import jsint.Pair;
import render_sdf.animation.Matrix4dAnimated;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import graphics.Graphics;
import graphics.Graphics3D;
import intersection.Intersection;
import utility.Util;

public class Polyline extends Curve {

	public boolean stroked = true;
	public boolean filled = false;

	private ArrayList<Vector3d> points = null;

	protected ArrayList<Line> hatchLines;

	protected boolean closed = false;

	private double[] segmentLengths;
	private double calculatedLength = -1;


	public Polyline() {
		super();
		this.points = new ArrayList<Vector3d>();
		setMatrix(new Matrix4dAnimated(new Matrix4d(),"Polyline"));
	}


	public Polyline(Vector3d[] vertices) {
		super();
		this.points = new ArrayList<Vector3d>(Arrays.asList(vertices));
		setupVAO();
		setMatrix(new Matrix4dAnimated(new Matrix4d(),"Polyline"));
	}


	public Polyline(Pair pair) {
		super();
		this.points = new ArrayList<Vector3d>();
		while (pair.first != null && pair != Pair.EMPTY) {
			points.add((Vector3d) pair.first);
			pair = (Pair) pair.rest;
		}

		setupVAO();
		setMatrix(new Matrix4dAnimated(new Matrix4d(),"Polyline"));
	}


	public void setVertices(ArrayList<Vector3d> vertices) {
		this.points = vertices;
	}
	
	
	public void addPoint(Vector3d v) {
		points.add(v);
	}


	@Override
	public void render(double time) {
		if (!visible) {
			return;
		}
		
		
		Graphics3D.drawPolyLine(glidVAO, points.size(), modelMatrix.get(time), colorFill);
	}


	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>(hatchLines));
	}


	@Override
	public Vector3d getLocalVectorOnCurve(double t, double time) {
		recalculateLength();

		double scaledPos = t * calculatedLength;

		for (int i = 0; i < segmentLengths.length; i++) {
			if (scaledPos < segmentLengths[i]) {
				return points.get(i).lerp(points.get(i+1), scaledPos / segmentLengths[i]);
			}
			else {
				scaledPos -= segmentLengths[i];
			}
		}
		System.out.println("Bad polyline parameter : " + t);
		return (null);
	}


	private void recalculateLength() {
		segmentLengths = new double[points.size() - 1];
		for (int i = 0; i < points.size() - 1; i++) {
			segmentLengths[i] = points.get(i).distance(points.get(i + 1));
		}
		calculatedLength = Util.arraySum(segmentLengths);
	}

	
	@SuppressWarnings("static-access")
	@Override
	public void setupVAO() {
		super.setupVAO();
		
		float[] vertices = new float[points.size() * 3];
		
		for (int i = 0; i < points.size(); i++) {
			Vector3d v = points.get(i);
			vertices[i * 3 + 0] = (float)v.x;
			vertices[i * 3 + 1] = (float)v.y;
			vertices[i * 3 + 2] = (float)v.z;
		}

		//explicitGeometry = new Polyline(explicitVectors);
		
		
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
