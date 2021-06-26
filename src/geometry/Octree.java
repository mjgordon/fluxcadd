package geometry;

import java.util.ArrayList;

import utility.PVector;

public class Octree extends Geometry {
	
	public Octree[] children;
	
	public byte data;
	
	public float size;
	
	public Box[] boxRepresentation;
	
	public Octree(float leafSize, int layers) {
		this.size = (float) (Math.pow(2, layers) * leafSize);
		
		this.data = 0;
	}
	
	private void calculateBoxRepresentation() {
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PVector[] getVectorRepresentation(float resolution) {
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

}
