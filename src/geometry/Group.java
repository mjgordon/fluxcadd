package geometry;

import java.util.ArrayList;

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
		for (Geometry g : geometry) {
			g.render();
		}
	}

	@Override
	public ArrayList<PVector> getVectorRepresentation(float resolution) {
		ArrayList<PVector> out = new ArrayList<PVector>();
		for (Geometry g : geometry) {
			out.addAll(g.getVectorRepresentation(resolution));
		}
		return out;
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		ArrayList<Line> out = new ArrayList<Line>();
		for (Geometry g : geometry) {
			out.addAll(g.getHatchLines());
		}
		return out;
	}

}
