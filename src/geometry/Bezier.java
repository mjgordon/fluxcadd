package geometry;

import java.util.ArrayList;

import utility.PVector;
import utility.Util;

public class Bezier extends Curve {

	private Point controlStart;
	private Point controlEnd;

	public Bezier(PVector start, PVector end, PVector controlStart, PVector controlEnd) {
		this.startPoint = new Point(start);
		this.endPoint = new Point(end);
		this.controlStart = new Point(controlStart);
		this.controlEnd = new Point(controlEnd);
		regenerateHelperVectors(10);
	}
	
	public Bezier(Point start, Point end, Point controlStart, Point controlEnd) {
		this.startPoint = start;
		this.endPoint = end;
		this.controlStart = controlStart;
		this.controlEnd = controlEnd;
		
		regenerateHelperVectors(10);
	}
	
	@Override
	public void render() {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Point getPointOnCurve(float t) {
		PVector mid1 = new PVector(Util.lerp(startPoint.x(), controlStart.x(), t), Util.lerp(startPoint.y(), controlStart.y(), t));
		PVector mid2 = new PVector(Util.lerp(controlStart.x(), controlEnd.x(), t), Util.lerp(controlStart.y(), controlEnd.y(), t));
		PVector mid3 = new PVector(Util.lerp(controlEnd.x(), endPoint.x(), t), Util.lerp(controlEnd.y(), endPoint.y(), t));

		PVector mid4 = new PVector(Util.lerp(mid1.x, mid2.x, t), Util.lerp(mid1.y, mid2.y, t));
		PVector mid5 = new PVector(Util.lerp(mid2.x, mid3.x, t), Util.lerp(mid2.y, mid3.y, t));

		return (new Point(new PVector(Util.lerp(mid4.x, mid5.x, t), Util.lerp(mid4.y, mid5.y, t))));
	}
	
	public void regenerateHelperVectors(int resolution) {
		helperVectors = new ArrayList<PVector>();
		for (int i = 0; i <= resolution; i++) {
			float t = (float) i / resolution;
			Point point = getPointOnCurve(t);
			helperVectors.add(point.getVector());
		}
	}

}
