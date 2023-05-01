package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.animation.Animatable;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import utility.Color;


public class SDFPrimitiveCube extends SDF {
	private Matrix4dAnimated frame;

	public SDFPrimitiveCube(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		base.m00(size / 2);
		base.m11(size / 2);
		base.m22(size / 2);
		
		this.frame = new Matrix4dAnimated(base, "Cube");
		
		this.material = material;
		
		displayName = "PrimCube";
	}
	
	public SDFPrimitiveCube(Vector3d position, double sizeX, double sizeY, double sizeZ, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		base.m00(sizeX / 2);
		base.m11(sizeY / 2);
		base.m22(sizeZ / 2);
		
		this.frame = new Matrix4dAnimated(base, "Cube");
		
		this.material = material;
		
		displayName = "PrimCube";
	}
	
	public void addKeyframe(double timestamp, Matrix4d m) {
		frame.addKeyframe(timestamp, m);
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vLocal = frame.getInvert(time).transformPosition(v, new Vector3d());
		double ax = Math.abs(vLocal.x);
		double ay = Math.abs(vLocal.y);
		double az = Math.abs(vLocal.z);
		boolean hx = ax < 1;
		boolean hy = ay < 1;
		boolean hz = az < 1;

		double distance;

		// Inside cube (heuristic)
		if (hx && hy && hz) {
			distance = Math.min(Math.min(ax, ay), az) - 1;
		}
		// In front of X face
		else if (hy && hz) {
			distance = ax - 1;
		}
		// In front of Y face
		else if (hx && hz) {
			distance = ay - 1;
		}
		// In front of Z face
		else if (hx && hy) {
			distance = az - 1;
		}
		// Off X edge
		else if (hx) {
			distance = Math.sqrt(Math.pow(ay - 1, 2) + Math.pow(az - 1, 2));
		}
		// Off Y edge
		else if (hy) {
			distance = Math.sqrt(Math.pow(ax - 1, 2) + Math.pow(az - 1, 2));
		}
		// Off Z edge
		else if (hz) {
			distance = Math.sqrt(Math.pow(ax - 1, 2) + Math.pow(ay - 1, 2));
		}
		// Off corner
		else {
			distance = Math.sqrt(Math.pow(ax - 1, 2) + Math.pow(ay - 1, 2) + Math.pow(az - 1, 2));
		}

		return distance;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		Color c = getPrimitiveColor(solid, materialPreview);

		g.add(new Line(new Vector3d(-1, -1, -1), new Vector3d(1, -1, -1)).setFillColor(c));
		g.add(new Line(new Vector3d(-1, 1, -1), new Vector3d(1, 1, -1)).setFillColor(c));
		g.add(new Line(new Vector3d(-1, -1, 1), new Vector3d(1, -1, 1)).setFillColor(c));
		g.add(new Line(new Vector3d(-1, 1, 1), new Vector3d(1, 1, 1)).setFillColor(c));

		g.add(new Line(new Vector3d(-1, -1, -1), new Vector3d(-1, 1, -1)).setFillColor(c));
		g.add(new Line(new Vector3d(1, -1, -1), new Vector3d(1, 1, -1)).setFillColor(c));
		g.add(new Line(new Vector3d(-1, -1, 1), new Vector3d(-1, 1, 1)).setFillColor(c));
		g.add(new Line(new Vector3d(1, -1, 1), new Vector3d(1, 1, 1)).setFillColor(c));

		g.add(new Line(new Vector3d(-1, -1, -1), new Vector3d(-1, -1, 1)).setFillColor(c));
		g.add(new Line(new Vector3d(1, -1, -1), new Vector3d(1, -1, 1)).setFillColor(c));
		g.add(new Line(new Vector3d(-1, 1, -1), new Vector3d(-1, 1, 1)).setFillColor(c));
		g.add(new Line(new Vector3d(1, 1, -1), new Vector3d(1, 1, 1)).setFillColor(c));

		g.setFrame(frame);

		gd.add(g);
	}
	
	@Override
	public Animatable[] getAnimated() {
		return new Animatable[] {frame};
	}

	
}
