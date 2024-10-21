package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import utility.Color3i;

public class SDFPrimitiveCube extends SDF {
	private Matrix4dAnimated frame;

	Vector3d dimensions;

	Vector3d helper = new Vector3d();


	public SDFPrimitiveCube(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		dimensions = new Vector3d(size / 2, size / 2, size / 2);

		this.frame = new Matrix4dAnimated(base, "Cube");

		this.material = material;

		displayName = "PrimCube";
	}


	public SDFPrimitiveCube(Vector3d position, double sizeX, double sizeY, double sizeZ, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		
		dimensions = new Vector3d(sizeX / 2, sizeY / 2, sizeZ / 2);

		this.frame = new Matrix4dAnimated(base, "Cube");

		this.material = material;

		displayName = "PrimCube";
	}


	public void addKeyframe(double timestamp, Matrix4d m) {
		frame.addKeyframe(timestamp, m);
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Matrix4d frameInvert = frame.getInvert(time);

		double x = dimensions.x;
		double y = dimensions.y;
		double z = dimensions.z;

		Vector3d vLocal = frameInvert.transformPosition(v, helper);

		double ax = Math.abs(vLocal.x);
		double ay = Math.abs(vLocal.y);
		double az = Math.abs(vLocal.z);
		boolean hx = ax < x;
		boolean hy = ay < y;
		boolean hz = az < z;

		double distance;

		// Inside cube (heuristic)
		if (hx && hy && hz) {
			distance = Math.min(Math.min(ax, ay), az) - 1;
		}
		// In front of X face
		else if (hy && hz) {
			distance = ax - x;
		}
		// In front of Y face
		else if (hx && hz) {
			distance = ay - y;
		}
		// In front of Z face
		else if (hx && hy) {
			distance = az - z;
		}
		// Off X edge
		else if (hx) {
			distance = Math.sqrt(Math.pow(ay - y, 2) + Math.pow(az - z, 2));
		}
		// Off Y edge
		else if (hy) {
			distance = Math.sqrt(Math.pow(ax - x, 2) + Math.pow(az - z, 2));
		}
		// Off Z edge
		else if (hz) {
			distance = Math.sqrt(Math.pow(ax - x, 2) + Math.pow(ay - y, 2));
		}
		// Off corner
		else {
			distance = Math.sqrt(Math.pow(ax - x, 2) + Math.pow(ay - y, 2) + Math.pow(az - z, 2));
		}

		return distance;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		Color3i c = getPrimitiveColor(solid, materialPreview);

		double x = dimensions.x;
		double y = dimensions.y;
		double z = dimensions.z;

		g.add(new Line(new Vector3d(-x, -y, -z), new Vector3d(x, -y, -z)).setFillColor(c));
		g.add(new Line(new Vector3d(-x, y, -z), new Vector3d(x, y, -z)).setFillColor(c));
		g.add(new Line(new Vector3d(-x, -y, z), new Vector3d(x, -y, z)).setFillColor(c));
		g.add(new Line(new Vector3d(-x, y, z), new Vector3d(x, y, z)).setFillColor(c));

		g.add(new Line(new Vector3d(-x, -y, -z), new Vector3d(-x, y, -z)).setFillColor(c));
		g.add(new Line(new Vector3d(x, -y, -z), new Vector3d(x, y, -z)).setFillColor(c));
		g.add(new Line(new Vector3d(-x, -y, z), new Vector3d(-x, y, z)).setFillColor(c));
		g.add(new Line(new Vector3d(x, -y, z), new Vector3d(x, y, z)).setFillColor(c));

		g.add(new Line(new Vector3d(-x, -y, -z), new Vector3d(-x, -y, z)).setFillColor(c));
		g.add(new Line(new Vector3d(x, -y, -z), new Vector3d(x, -y, z)).setFillColor(c));
		g.add(new Line(new Vector3d(-x, y, -z), new Vector3d(-x, y, z)).setFillColor(c));
		g.add(new Line(new Vector3d(x, y, -z), new Vector3d(x, y, z)).setFillColor(c));

		g.setMatrix(frame);

		gd.add(g);
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
}
