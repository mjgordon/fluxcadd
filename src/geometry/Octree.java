package geometry;

import java.util.ArrayList;

import org.joml.Vector3d;

import intersection.Intersection;

public class Octree extends Geometry {

	public Octree[] children;

	public byte data;

	public float size;

	public Box[] boxRepresentation;


	public Octree(float leafSize, int layers) {
		this.size = (float) (Math.pow(2, layers) * leafSize);

		this.data = 0;
	}


	@Override
	public void render(double time) {
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
	public void recalculateExplicitGeometry() {
		// TODO Auto-generated method stub

	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}

}
