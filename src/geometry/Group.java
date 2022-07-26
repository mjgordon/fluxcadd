package geometry;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import intersection.Intersection;
import utility.PMatrix3D;
import utility.PVector;

public class Group extends Geometry {

	private ArrayList<Geometry> geometry;
	
	public Group() {
		geometry = new ArrayList<Geometry>();
	}
	
	public Group(ArrayList<Geometry> geometry) {
		this.geometry = geometry;
	}
	
	public void add(Geometry g) {
		geometry.add(g);
	}
	
	@Override
	public void render() {
		GL11.glPushMatrix();
		
		float[] frameArray = new float[16];
		
		PMatrix3D frameTemp = frame.get();
		frameTemp.transpose();
		
		frameTemp.get(frameArray);
		
		GL11.glMultMatrixf(frameArray);
		
		for (Geometry g : geometry) {
			g.render();
		}
		
		GL11.glPopMatrix();
	}

	@Override
	public PVector[] getVectorRepresentation(float resolution) {
		ArrayList<PVector> out = new ArrayList<PVector>();
		for (Geometry g : geometry) {
			out.addAll(Arrays.asList(g.getVectorRepresentation(resolution)));
		}
		return out.toArray(new PVector[out.size()]);
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		ArrayList<Line> out = new ArrayList<Line>();
		for (Geometry g : geometry) {
			out.addAll(g.getHatchLines());
		}
		return out;
	}

	@Override
	public void recalculateExplicitGeometry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Intersection intersectLine(PVector start, PVector end) {
		// TODO Auto-generated method stub
		return null;
	}

}
