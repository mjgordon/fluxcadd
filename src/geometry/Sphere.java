package geometry;

import java.util.ArrayList;

import org.joml.Vector3d;

import graphics.Graphics3D;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
import utility.Color3i;

public class Sphere extends Geometry {
	
	public Sphere(Matrix4dAnimated modelMatrixInput, Color3i colorFill, double radius) {
		
		setMatrix(new Matrix4dAnimated(modelMatrixInput).scale(radius, radius, radius));
		
		this.colorFill = colorFill;
	}

	@Override
	public void render(double time) {
		Graphics3D.drawSphere(modelMatrix.get(time), colorFill);
		
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

}
