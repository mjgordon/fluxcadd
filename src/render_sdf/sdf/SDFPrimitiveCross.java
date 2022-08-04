package render_sdf.sdf;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveCross extends SDF {
	private Matrix4d frame;
	private Matrix4d frameInvert;
	private float size;

	private float previewSize = 300;


	public SDFPrimitiveCross(Vector3d position, float size, Material material) {
		this.frame = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		this.frameInvert = frame.invert(new Matrix4d());
		this.size = size;
		this.material = material;
	}


	public SDFPrimitiveCross(Matrix4d frame, float size, Material material) {
		this.frame = frame;
		this.frameInvert = frame.invert(new Matrix4d());
		this.size = size;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(Vector3d v) {

		Vector3d vLocal = new Vector3d(v).mulPosition(frameInvert);

		double ax = abs(vLocal.x);
		double ay = abs(vLocal.y);
		double az = abs(vLocal.z);

		return (new DistanceData(min(min(ax + ay, ay + az), ax + az) - size, this.material));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		Group g = new Group();

		float hp = previewSize / 2;

		Color c = solid ? previewColorSolid : previewColorVoid;

		g.add(new Line(new Vector3d(-hp, 0, 0), new Vector3d(hp, 0, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, -hp, 0), new Vector3d(0, hp, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, 0, -hp), new Vector3d(0, 0, hp)).setFillColor(c));

		g.setFrame(frame);

		gd.add(g);
	}
}
