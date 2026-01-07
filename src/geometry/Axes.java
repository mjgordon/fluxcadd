package geometry;

import java.util.ArrayList;

import org.joml.Vector3d;

import graphics.Graphics3D;
import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;
import utility.Color3i;

public class Axes extends Geometry {
	
	float s = 1.0f;
	
	public Axes(Matrix4dAnimated m, Color3i color, float s) {
		this.modelMatrix = m;
		this.colorFill = color;
		this.s = s;
		
	}

	@Override
	public void render(double time) {
		Graphics3D.drawCross(modelMatrix.get(time).scale(s), colorFill);
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
