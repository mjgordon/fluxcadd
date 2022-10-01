package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveCube extends SDF {
	private Matrix4d frame;
	private Matrix4d frameInvert;
	private double halfSize;


	public SDFPrimitiveCube(Vector3d position, double size, Material material) {
		this.frame = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		this.frameInvert = new Matrix4d(frame).invert();
		this.halfSize = size / 2;
		this.material = material;
	}


	public SDFPrimitiveCube(Matrix4d frame, double size, Material material) {
		this.frame = frame;
		this.frameInvert = new Matrix4d(frame).invert();
		this.halfSize = size / 2;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(Vector3d v) {
		Vector3d vLocal = frameInvert.transformPosition(v, new Vector3d());
		double ax = Math.abs(vLocal.x);
		double ay = Math.abs(vLocal.y);
		double az = Math.abs(vLocal.z);
		boolean hx = ax < halfSize;
		boolean hy = ay < halfSize;
		boolean hz = az < halfSize;

		double distance;

		// Inside cube (heuristic)
		if (hx && hy && hz) {
			distance = Math.min(Math.min(ax, ay), az) - halfSize;
		}
		// In front of X face
		else if (hy && hz) {
			distance = ax - halfSize;
		}
		// In front of Y face
		else if (hx && hz) {
			distance = ay - halfSize;
		}
		// In front of Z face
		else if (hx && hy) {
			distance = az - halfSize;
		}
		// Off X edge
		else if (hx) {
			distance = Math.sqrt(Math.pow(ay - halfSize, 2) + Math.pow(az - halfSize, 2));
		}
		// Off Y edge
		else if (hy) {
			distance = Math.sqrt(Math.pow(ax - halfSize, 2) + Math.pow(az - halfSize, 2));
		}
		// Off Z edge
		else if (hz) {
			distance = Math.sqrt(Math.pow(ax - halfSize, 2) + Math.pow(ay - halfSize, 2));
		}
		// Off corner
		else {
			distance = Math.sqrt(Math.pow(ax - halfSize, 2) + Math.pow(ay - halfSize, 2) + Math.pow(az - halfSize, 2));
		}

		return (new DistanceData(distance, this.material));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		Group g = new Group();

		Color c = getPrimitiveColor(solid, materialPreview);

		g.add(new Line(new Vector3d(-halfSize, -halfSize, -halfSize), new Vector3d(halfSize, -halfSize, -halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(-halfSize, halfSize, -halfSize), new Vector3d(halfSize, halfSize, -halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(-halfSize, -halfSize, halfSize), new Vector3d(halfSize, -halfSize, halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(-halfSize, halfSize, halfSize), new Vector3d(halfSize, halfSize, halfSize)).setFillColor(c));

		g.add(new Line(new Vector3d(-halfSize, -halfSize, -halfSize), new Vector3d(-halfSize, halfSize, -halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(halfSize, -halfSize, -halfSize), new Vector3d(halfSize, halfSize, -halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(-halfSize, -halfSize, halfSize), new Vector3d(-halfSize, halfSize, halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(halfSize, -halfSize, halfSize), new Vector3d(halfSize, halfSize, halfSize)).setFillColor(c));

		g.add(new Line(new Vector3d(-halfSize, -halfSize, -halfSize), new Vector3d(-halfSize, -halfSize, halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(halfSize, -halfSize, -halfSize), new Vector3d(halfSize, -halfSize, halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(-halfSize, halfSize, -halfSize), new Vector3d(-halfSize, halfSize, halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(halfSize, halfSize, -halfSize), new Vector3d(halfSize, halfSize, halfSize)).setFillColor(c));

		g.setFrame(frame);

		gd.add(g);
	}
}
