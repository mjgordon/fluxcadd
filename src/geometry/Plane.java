package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import graphics.Graphics3D;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
import utility.Color3i;

public class Plane extends Geometry {
	
	float displaySize;
	
	public Plane(Matrix4dAnimated modelMatrix, Color3i colorFill, float displaySize) {
		setMatrix(modelMatrix);
		this.colorFill = colorFill;
		this.displaySize = displaySize;
	}

	@Override
	public void render(double time) {
		Graphics3D.drawGrid(new Matrix4d(modelMatrix.get(time)).scale(displaySize), colorFill);	
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
