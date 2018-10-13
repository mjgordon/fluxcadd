package geometry;

import java.util.ArrayList;

import utility.PVector;

public class Rect extends Polyline {
	public Rect(float x, float y, float width, float height) {
		position = new PVector(x,y);
		size = new PVector(width,height);
		
		setVertices(regenerate());
	}
	
	private ArrayList<PVector> regenerate() {
		ArrayList<PVector> out = new ArrayList<PVector>();
		
		out.add(new PVector(position.x,position.y));
		out.add(new PVector(position.x + size.x,position.y));
		out.add(new PVector(position.x + size.x,position.y + size.y));
		out.add(new PVector(position.x,position.y + size.y));
		
		return(out);
	}
}
