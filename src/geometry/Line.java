package geometry;

import java.util.ArrayList;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;
import utility.math.UtilMath;



/**
 * Implicit from seperately animatable Geometry objects, doesn't use frame
 * 
 * @author mattj
 *
 */
public class Line extends Curve {

	public Point startPoint = null;
	public Point endPoint = null;

	public Vector3d startVectorExplicit = new Vector3d(0,0,0);
	public Vector3d endVectorExplicit = new Vector3d(0,0,0);


	public Line(Point a, Point b) {
		this.startPoint = a;
		this.endPoint = b;
		recalculateExplicitGeometry();
	}


	public Line(Vector3d a, Vector3d b) {
		this.startVectorExplicit = a;
		this.endVectorExplicit = b;
	}


	public void render(double time) {
		if (!visible) {
			return;
		}

		if (colorFill != null) {
			OGLWrapper.glColor(colorFill);
			OGLWrapper.glLineWidth(displayWidth);

			GL11.glBegin(GL11.GL_LINES);
			
			Vector3d a = (startPoint == null) ? startVectorExplicit : startPoint.getVector(time);
			Vector3d b = (endPoint == null) ? endVectorExplicit : endPoint.getVector(time);
			
			GL11.glVertex3d(a.x, a.y, a.z);
			GL11.glVertex3d(b.x, b.y, b.z);
			GL11.glEnd();
		}
	}


	public Vector3d xyIntersect(double z) {
		Vector3d diff = new Vector3d(endVectorExplicit).sub(startVectorExplicit);

		if (diff.z == 0) {
			return null;
		}
		diff.div(diff.z);
		double dz = z - startVectorExplicit.z;
		diff.mul(dz);

		diff.add(startVectorExplicit);

		if (endVectorExplicit.z > startVectorExplicit.z) {
			if (diff.z >= startVectorExplicit.z && diff.z <= endVectorExplicit.z) {
				return (diff);
			}
			else {
				return (null);
			}

		}
		else {
			if (diff.z >= endVectorExplicit.z && diff.z <= startVectorExplicit.z) {
				return (diff);
			}
			else {
				return (null);
			}
		}
	}


	public Vector3d radialIntersect(double r) {
		r += UtilMath.HALF_PI;
		Vector3d n = new Vector3d(Math.cos(r), Math.sin(r), 0);

		Vector3d ba = new Vector3d(endVectorExplicit).sub(startVectorExplicit);

		double nDotA = n.dot(startVectorExplicit);
		double nDotBA = n.dot(ba);

		ba.mul(-nDotA / nDotBA);
		ba.add(startVectorExplicit);

		if (pointOnLineFast(ba) == false) {
			ba = null;
		}

		return (ba);
	}


	public boolean pointOnLineFast(Vector3d point) {
		if (startVectorExplicit.x < endVectorExplicit.x) {
			if (point.x < startVectorExplicit.x || point.x > endVectorExplicit.x) {
				return (false);
			}
		}
		else if (point.x > startVectorExplicit.x || point.x < endVectorExplicit.x) {
			return (false);
		}

		if (startVectorExplicit.y < endVectorExplicit.y) {
			if (point.y < startVectorExplicit.y || point.y > endVectorExplicit.y) {
				return (false);
			}

		}
		else if (point.y > startVectorExplicit.y || point.y < endVectorExplicit.y) {
			return (false);
		}

		if (startVectorExplicit.z < endVectorExplicit.z) {
			if (point.z < startVectorExplicit.z || point.z > endVectorExplicit.z) {
				return (false);
			}

		}
		else if (point.z > startVectorExplicit.z || point.z < endVectorExplicit.z) {
			return (false);
		}

		return (true);
	}


	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>());
	}


	@Override
	public Vector3d getLocalVectorOnCurve(double t, double time) {
		Vector3d a = (startPoint == null) ? startVectorExplicit : startPoint.getVector(time);
		Vector3d b = (endPoint == null) ? endVectorExplicit : endPoint.getVector(time);
		
		return a.lerp(b, t, new Vector3d());
	}


	@Override
	public void recalculateExplicitGeometry() {
		explicitGeometry = this;
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}

}
