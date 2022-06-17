package geometry;

import java.util.ArrayList;

import intersection.Intersection;
import utility.PVector;

/**
 * Four-point bezier curve segment
 * 
 *
 */
public class Bezier extends Curve {
	
	private Point anchorStart = null;
	private Point anchorEnd = null;

	private Point controlStart = null;
	private Point controlEnd = null;
	
	private PVector anchorStartExplicit;
	private PVector anchorEndExplicit;
	private PVector controlStartExplicit;
	private PVector controlEndExplicit;
	

	public Bezier(PVector start, PVector end, PVector controlStart, PVector controlEnd) {
		this.anchorStart = new Point(start);
		this.anchorEnd = new Point(end);
		this.controlStart = new Point(controlStart);
		this.controlEnd = new Point(controlEnd);
		recalculateExplicitGeometry();
	}
	
	public Bezier(Point start, Point end, Point controlStart, Point controlEnd) {
		this.anchorStart = start;
		this.anchorEnd = end;
		this.controlStart = controlStart;
		this.controlEnd = controlEnd;
		
		recalculateExplicitGeometry();
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
	public PVector getVectorOnCurve(float t) {
		
		PVector mid1 = PVector.lerp(anchorStartExplicit, controlStartExplicit, t);
		PVector mid2 = PVector.lerp(controlStartExplicit, controlEndExplicit, t);
		PVector mid3 = PVector.lerp(controlEndExplicit, anchorEndExplicit, t);

		PVector mid4 = PVector.lerp(mid1, mid2, t);
		PVector mid5 = PVector.lerp(mid2, mid3, t);
		
		return(PVector.lerp(mid4, mid5, t));
	}
	
	public void recalculateExplicitGeometry() {
		int resolution = 10;
		
		anchorStartExplicit = frame.mult(anchorStart.getVector(), null);
		anchorEndExplicit = frame.mult(anchorEnd.getVector(), null);
		
		controlStartExplicit = frame.mult(controlStart.getVector(), null);
		controlEndExplicit = frame.mult(controlEnd.getVector(), null);
		
		explicitVectors = new PVector[resolution + 1];
		for (int i = 0; i <= resolution; i++) {
			float t = (float) i / resolution;
			explicitVectors[i] = getVectorOnCurve(t);
		}
		
		explicitGeometry = new Polyline(explicitVectors);
		
	}

	@Override
	public Intersection intersectLine(PVector start, PVector end) {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
