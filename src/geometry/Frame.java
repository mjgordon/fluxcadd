package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import intersection.Intersection;

@Deprecated
public class Frame extends Geometry {

	public Frame(Matrix4d frame) {
		this.frame = frame;
	}


	@Override
	public void render() {
		renderFrame();
	}


	@Override
	public void recalculateExplicitGeometry() {
		// TODO Auto-generated method stub

	}


	@Override
	public Vector3d[] getVectorRepresentation(double resolution) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}

}
