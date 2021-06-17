package geometry;

import java.util.ArrayList;

import utility.Color;
import utility.PVector;
import utility.Util;

import static org.lwjgl.opengl.GL11.*;

public class Line extends Curve {
	
	public Point startPoint = null;;
	public Point endPoint = null;;
	
	private PVector startVectorExplicit;
	private PVector endVectorExplicit;

	
	public Line(Point a, Point b) {
		this.startPoint = a;
		this.endPoint = b;
		recalculateExplicitGeometry();
	}
	
	public Line(PVector a, PVector b) {
		this.startVectorExplicit = a;
		this.endVectorExplicit = a;
	}


	public void render() {
		if (!visible)
			return;
		if (color != null) {
			Color.setGlColor(color);
			glLineWidth(displayWidth);
			glBegin(GL_LINES);
			glVertex3f(startVectorExplicit.x, startVectorExplicit.y, startVectorExplicit.z);
			glVertex3f(endVectorExplicit.x, endVectorExplicit.y, endVectorExplicit.z);
			glEnd();
			glLineWidth(1);
		}

	}

//	public float getM() {
//		float dy = endPoint.y() - startPoint.y();
//		float dx = endPoint.x() - startPoint.x();
//		return (dy / dx);
//	}

//	public float xValueAtY(float y) {
//		float m = getM();
//		float dy = startPoint.y() - y;
//		float f = dy / m;
//		return (startPoint.x() - f);
//	}

	/***
	 * This still needs testing
	 * 
	 * @param x
	 * @return
	 */
//	public float yValueAtX(float x) {
//		float m = getM();
//		float dx = startPoint.x() - x;
//		float f = m / dx;
//		return (startPoint.y() - f);
//	}

	public PVector xyIntersect(float z) {
		PVector startVector = startPoint.getVector();
		PVector endVector = endPoint.getVector();
		PVector angle = PVector.sub(endVector, startVector);
		if (angle.z == 0)
			return null;
		angle.div(angle.z);
		float dz = z - startPoint.z();
		angle.mult(dz);
		PVector intersect = PVector.add(startVector, angle);
		if (endPoint.z() > startPoint.z()) {
			if (intersect.z >= startPoint.z() && intersect.z <= endPoint.z()) {
				return (intersect);
			} else
				return (null);
		} else {
			if (intersect.z >= endPoint.z() && intersect.z <= startPoint.z()) {
				return (intersect);
			} else
				return (null);
		}
	}

	public PVector radialIntersect(float r) {
		PVector startVector = startPoint.getVector();
		PVector endVector = endPoint.getVector();
		r += Util.HALF_PI;
		PVector n = new PVector(Math.cos(r), Math.sin(r), 0);
		PVector ba = PVector.sub(endVector, startVector);
		float nDotA = PVector.dot(n, startVector);
		float nDotBA = PVector.dot(n, ba);

		PVector out = PVector.mult(ba, -nDotA / nDotBA);
		out.add(startVector);
		if (pointOnLineFast(out) == false)
			out = null;
		return (out);
	}

//	public boolean containsX(float x) {
//		return ((x >= startPoint.x() && x <= endPoint.x()) || (x >= endPoint.x() && x <= startPoint.x()));
//	}

	public boolean pointOnLineFast(PVector point) {
		if (startVectorExplicit.x < endVectorExplicit.x) {
			if (point.x < startVectorExplicit.x || point.x > endVectorExplicit.x)
				return (false);
		} else if (point.x > startVectorExplicit.x || point.x < endVectorExplicit.x)
			return (false);
		if (startVectorExplicit.y < endVectorExplicit.y) {
			if (point.y < startVectorExplicit.y || point.y > endVectorExplicit.y)
				return (false);
		} else if (point.y > startVectorExplicit.y || point.y < endVectorExplicit.y)
			return (false);
		if (startVectorExplicit.z < endVectorExplicit.z) {
			if (point.z < startVectorExplicit.z || point.z > endVectorExplicit.z)
				return (false);
		} else if (point.z > startVectorExplicit.z || point.z < endVectorExplicit.z)
			return (false);

		return (true);
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>());
	}

	
	@Override
	public PVector getVectorOnCurve(float p) {
		float x = Util.lerp(startPoint.x(), endPoint.x(), p);
		float y = Util.lerp(startPoint.y(), endPoint.y(), p);
		float z = Util.lerp(startPoint.z(), endPoint.z(), p);
		return(new PVector(x,y,z));
	}

	@Override
	public void recalculateExplicitGeometry() {
		explicitGeometry = this;
		
		startVectorExplicit = frame.mult(startPoint.getVector(), null);
		endVectorExplicit = frame.mult(endPoint.getVector(), null);

		
	}

}
