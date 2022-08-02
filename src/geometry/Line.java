package geometry;

import java.util.ArrayList;

import org.joml.Vector3d;

import graphics.OGLWrapper;
import intersection.Intersection;
import utility.math.UtilMath;

import static org.lwjgl.opengl.GL11.*;

public class Line extends Curve {

	public Point startPoint = null;;
	public Point endPoint = null;;

	public Vector3d startVectorExplicit;
	public Vector3d endVectorExplicit;


	public Line(Point a, Point b) {
		this.startPoint = a;
		this.endPoint = b;
		recalculateExplicitGeometry();
	}


	public Line(Vector3d a, Vector3d b) {
		this.startVectorExplicit = a;
		this.endVectorExplicit = b;
	}


	public void render() {
		if (!visible) {
			return;
		}

		if (colorFill != null) {
			OGLWrapper.glColor(colorFill);
			glLineWidth(displayWidth);

			glPushMatrix();
			{
				glMultMatrixd(frame.get(new double[16]));
				glBegin(GL_LINES);
				glVertex3d(startVectorExplicit.x, startVectorExplicit.y, startVectorExplicit.z);
				glVertex3d(endVectorExplicit.x, endVectorExplicit.y, endVectorExplicit.z);
				glEnd();

			}
			glPopMatrix();
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
	public Vector3d getVectorOnCurve(double p) {
		double x = UtilMath.lerp(startPoint.x(), endPoint.x(), p);
		double y = UtilMath.lerp(startPoint.y(), endPoint.y(), p);
		double z = UtilMath.lerp(startPoint.z(), endPoint.z(), p);
		return (new Vector3d(x, y, z));
	}


	@Override
	public void recalculateExplicitGeometry() {
		explicitGeometry = this;

		startVectorExplicit = frame.transformPosition(startPoint.getPositionVector());
		endVectorExplicit = frame.transformPosition(endPoint.getPositionVector());
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}

}
