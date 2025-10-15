package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import graphics.Graphics3D;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
import utility.Color3i;

public class Octahedron extends Geometry {
	
	public Octahedron(Matrix4d modelMatrix, Color3i colorFill, float axesSize) {
		this.modelMatrix = new Matrix4dAnimated(modelMatrix.scale(axesSize), "Diamond");
		this.colorFill = colorFill;
	}

	@Override
	public void render(double time) {
		Graphics3D.drawDiamond(modelMatrix.get(time), colorFill);	
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
