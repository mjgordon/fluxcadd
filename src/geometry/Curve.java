package geometry;

import java.util.ArrayList;

import utility.PVector;
import utility.Util;

/**
 * Bezier Curves
 * @author mgordon
 *
 */
public class Curve extends Polyline {

	private PVector start;
	private PVector end;
	private PVector controlStart;
	private PVector controlEnd;

	public Curve(PVector start, PVector end, PVector controlStart, PVector controlEnd) {
		this.start = start;
		this.end = end;
		this.controlStart = controlStart;
		this.controlEnd = controlEnd;
		
		setVertices(regenerate(10));
	}
	
	public ArrayList<PVector> regenerate(int resolution) {
		ArrayList<PVector> out = new ArrayList<PVector>();
		for (int i = 0; i < resolution; i++) {
			float t = (float) i / resolution;
			PVector point = evaluateBezier(start,end,controlStart,controlEnd,t);
			out.add(point);
		}
		return(out);
	}
	

	public PVector evaluateBezier(PVector p1, PVector p2, PVector cp1, PVector cp2, float t) {
		PVector mid1 = new PVector(Util.lerp(p1.x, cp1.x, t), Util.lerp(p1.y, cp1.y, t));
		PVector mid2 = new PVector(Util.lerp(cp1.x, cp2.x, t), Util.lerp(cp1.y, cp2.y, t));
		PVector mid3 = new PVector(Util.lerp(cp2.x, p2.x, t), Util.lerp(cp2.y, p2.y, t));

		PVector mid4 = new PVector(Util.lerp(mid1.x, mid2.x, t), Util.lerp(mid1.y, mid2.y, t));
		PVector mid5 = new PVector(Util.lerp(mid2.x, mid3.x, t), Util.lerp(mid2.y, mid3.y, t));

		return (new PVector(Util.lerp(mid4.x, mid5.x, t), Util.lerp(mid4.y, mid5.y, t)));
	}
	

}
