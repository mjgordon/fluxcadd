package geometry;

import java.util.ArrayList;

import intersection.Intersection;
import utility.PMatrix3D;
import utility.PVector;

public class Frame extends Geometry {
	
	public Frame(PMatrix3D frame) {
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
	public Intersection intersectLine(PVector start, PVector end) {
		// TODO Auto-generated method stub
		return null;
	}

}
