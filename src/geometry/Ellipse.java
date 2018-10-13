package geometry;

import java.util.ArrayList;

import utility.PVector;
import utility.Util;

public class Ellipse extends Polyline {

	private float cx;
	private float cy;

	public Ellipse(float x, float y, float width, float height) {
		this.cx = x;
		this.cy = y;
		size.x = width;
		size.y = height;
		
		closed = true;
		
		setVertices(regenerate(30));
	}

	public ArrayList<PVector> regenerate(int resolution) {
		ArrayList<PVector>points = new ArrayList<PVector>();

		for (int i = 0; i < resolution; i++) {
			float f = ((float) i / resolution) * Util.TWO_PI;
			PVector v = new PVector((float) (cx + (Math.cos(f) * size.x)), (float) (cy + (Math.sin(f) * size.y)));
			points.add(v);
		}

		return(points);
	}

}
