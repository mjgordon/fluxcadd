package geometry;

import java.util.ArrayList;

import utility.PVector;

public class Rect extends Polyline {
	public Rect(float x, float y, float width, float height) {
		position = new PVector(x,y);
		size = new PVector(width,height);
		closed = true;
		setVertices(regenerate());
	}
	
	private ArrayList<Point> regenerate() {
		ArrayList<Point> out = new ArrayList<Point>();
		
		out.add(new Point(new PVector(position.x,position.y)));
		out.add(new Point(new PVector(position.x + size.x,position.y)));
		out.add(new Point(new PVector(position.x + size.x,position.y + size.y)));
		out.add(new Point(new PVector(position.x,position.y + size.y)));
		out.add(new Point(new PVector(position.x,position.y)));
		
		return(out);
	}
}
