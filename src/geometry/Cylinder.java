package geometry;

import java.util.ArrayList;
import org.joml.Vector3d;

import graphics.Graphics3D;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
import utility.Color3i;

public class Cylinder extends Geometry {
	
	public Cylinder(Matrix4dAnimated modelMatrixAnimated, Color3i colorFill) {
		setMatrix(modelMatrixAnimated);
		this.colorFill = colorFill;
	}

	@Override
	public void render(double time) {
		Graphics3D.drawCylinder(modelMatrix.get(time), colorFill);	
		this.renderFrame(time);
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
