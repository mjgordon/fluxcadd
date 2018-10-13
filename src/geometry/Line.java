package geometry;

import java.util.ArrayList;

import utility.PVector;
import utility.Util;

import static org.lwjgl.opengl.GL11.*;

public class Line extends Geometry {

	public PVector startPoint;
	public PVector endPoint;
	public int width = 1;

	public Line(Point a, Point b) {
		super();
		this.startPoint = a.position;
		this.endPoint = b.position;
	}

	public Line(PVector startPoint, PVector endPoint) {
		super();
		this.startPoint = startPoint.copy();
		this.endPoint = endPoint.copy();
	}

	public void render() {
		if (!visible)
			return;
		glColor3f(r, g, b);
		glLineWidth(width);
		glBegin(GL_LINES);
		glVertex3f(startPoint.x, startPoint.y, startPoint.z);
		glVertex3f(endPoint.x, endPoint.y, endPoint.z);
		glEnd();
		glLineWidth(1);
	}

	public float getM() {
		float dy = endPoint.y - startPoint.y;
		float dx = endPoint.x - startPoint.x;
		return (dy / dx);
	}

	public float xValueAtY(float y) {
		float m = getM();
		float dy = startPoint.y - y;
		float f = dy / m;
		return (startPoint.x - f);
	}

	/***
	 * This still needs testing
	 * 
	 * @param x
	 * @return
	 */
	public float yValueAtX(float x) {
		float m = getM();
		float dx = startPoint.x - x;
		float f = m / dx;
		return (startPoint.y - f);
	}

	public PVector xyIntersect(float z) {
		PVector angle = PVector.sub(endPoint, startPoint);
		if (angle.z == 0)
			return null;
		angle.div(angle.z);
		float dz = z - startPoint.z;
		angle.mult(dz);
		PVector intersect = PVector.add(startPoint, angle);
		if (endPoint.z > startPoint.z) {
			if (intersect.z >= startPoint.z && intersect.z <= endPoint.z) {
				return (intersect);
			} else
				return (null);
		} else {
			if (intersect.z >= endPoint.z && intersect.z <= startPoint.z) {
				return (intersect);
			} else
				return (null);
		}
	}

	public PVector radialIntersect(float r) {
		r += Util.HALF_PI;
		PVector n = new PVector(Math.cos(r), Math.sin(r), 0);
		PVector ba = PVector.sub(endPoint, startPoint);
		float nDotA = PVector.dot(n, startPoint);
		float nDotBA = PVector.dot(n, ba);

		PVector out = PVector.mult(ba, -nDotA / nDotBA);
		out.add(startPoint);
		if (pointOnLineFast(out) == false)
			out = null;
		return (out);
	}

	public boolean containsX(float x) {
		return ((x >= startPoint.x && x <= endPoint.x) || (x >= endPoint.x && x <= startPoint.x));
	}

	public boolean pointOnLineFast(PVector point) {
		if (startPoint.x < endPoint.x) {
			if (point.x < startPoint.x || point.x > endPoint.x)
				return (false);
		} else if (point.x > startPoint.x || point.x < endPoint.x)
			return (false);
		if (startPoint.y < endPoint.y) {
			if (point.y < startPoint.y || point.y > endPoint.y)
				return (false);
		} else if (point.y > startPoint.y || point.y < endPoint.y)
			return (false);
		if (startPoint.z < endPoint.z) {
			if (point.z < startPoint.z || point.z > endPoint.z)
				return (false);
		} else if (point.z > startPoint.z || point.z < endPoint.z)
			return (false);

		return (true);
	}

	@Override
	public ArrayList<PVector> getVectorRepresentation(float resolution) {
		ArrayList<PVector> out = new ArrayList<PVector>();
		out.add(startPoint.copy());
		out.add(endPoint.copy());
		return (out);
	}

	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>());
	}

}
