package geometry;

import java.util.ArrayList;

import utility.PMatrix3D;

import utility.PVector;

public class Rect extends Polyline {
	
	public Rect(float x, float y, float width, float height) {
		frame = new PMatrix3D(width,0,0,x,
							 0,height,0,y,
							 0,0,1,0,
							 0,0,0,1);
		closed = true;
		
		recalculateExplicitGeometry();
	}
	
	@Override
	public void recalculateExplicitGeometry() {
		explicitGeometry = new Polyline();
		
		ArrayList<Point> out = new ArrayList<Point>();
		
		out.add(new Point(new PVector(frame.m03,frame.m13)));
		out.add(new Point(new PVector(frame.m03 + frame.m00,frame.m13)));
		out.add(new Point(new PVector(frame.m03 + frame.m00,frame.m13 + frame.m11)));
		out.add(new Point(new PVector(frame.m03,frame.m13 + frame.m11)));
		out.add(new Point(new PVector(frame.m03,frame.m13)));
	}
}
