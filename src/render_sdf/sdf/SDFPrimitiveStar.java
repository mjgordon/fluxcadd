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
import utility.Color;

public class SDFPrimitiveStar extends SDF {
	private Matrix4dAnimated frame;
	private double halfSize;
	private double sphereSize;


	public SDFPrimitiveStar(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Star");
		this.halfSize = size / 2;
		this.sphereSize = halfSize * Math.sqrt(2);
		this.material = material;
		
		displayName = "PrimStar";
	}
	
	public SDFPrimitiveStar(Matrix4d base, double size, Material material) {
		frame = new Matrix4dAnimated(base, "Star");
		
		this.halfSize = size / 2;
		this.sphereSize = halfSize * Math.sqrt(2);
		this.material = material;
		
		displayName = "PrimStar";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vLocal = v.mulPosition(frame.getInvert(time), new Vector3d());
		double ax = Math.abs(vLocal.x);
		double ay = Math.abs(vLocal.y);
		double az = Math.abs(vLocal.z);

		double distance;

		// Main curved surface, only closest when within the virtual cube of the shape
		if (ax <= halfSize && ay <= halfSize && az <= halfSize) {
			distance = sphereSize - Math.sqrt(Math.pow(ax - halfSize, 2) + Math.pow(ay - halfSize, 2) + Math.pow(az - halfSize, 2));
		}
		// Otherwise, distance is to one of the points
		else if (ax >= ay && ax >= az) {
			distance = Math.sqrt(Math.pow(ax - halfSize, 2) + (ay * ay) + (az * az));
		}
		else if (ay >= ax && ay >= az) {
			distance = Math.sqrt((ax * ax) + Math.pow(ay - halfSize, 2) + (az * az));
		}
		else {
			distance = Math.sqrt((ax * ax) + (ay * ay) + Math.pow(az - halfSize, 2));
		}

		return distance;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		Color c = getPrimitiveColor(solid, materialPreview);

		g.add(new Line(new Vector3d(-halfSize, 0, 0), new Vector3d(halfSize, 0, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, -halfSize, 0), new Vector3d(0, halfSize, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, 0, -halfSize), new Vector3d(0, 0, halfSize)).setFillColor(c));

		g.setFrame(frame);

		gd.add(g);
	}
	
	@Override
	public Animated[] getAnimated() {
		return new Animated[] {frame};
	}
}
