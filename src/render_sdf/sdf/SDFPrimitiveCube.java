package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveCube extends SDF {
	private Vector3d position;
	private double size;


	public SDFPrimitiveCube(Vector3d position, double size, Material material) {
		this.position = position;
		this.size = size;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(Vector3d v) {
		Vector3d diff = new Vector3d(v).sub(position);

		return (new DistanceData(Math.max(Math.abs(diff.x), Math.max(Math.abs(diff.y), Math.abs(diff.z))) - size, this.material));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		Group g = new Group();

		float hp = (float) (size / 2);

		Color c = solid ? previewColorSolid : previewColorVoid;

		g.add(new Line(new Vector3d(-hp, -hp, -hp), new Vector3d(hp, -hp, -hp)).setColor(c));
		g.add(new Line(new Vector3d(-hp, hp, -hp), new Vector3d(hp, hp, -hp)).setColor(c));
		g.add(new Line(new Vector3d(-hp, -hp, hp), new Vector3d(hp, -hp, hp)).setColor(c));
		g.add(new Line(new Vector3d(-hp, hp, hp), new Vector3d(hp, hp, hp)).setColor(c));

		g.add(new Line(new Vector3d(-hp, -hp, -hp), new Vector3d(-hp, hp, -hp)).setColor(c));
		g.add(new Line(new Vector3d(hp, -hp, -hp), new Vector3d(hp, hp, -hp)).setColor(c));
		g.add(new Line(new Vector3d(-hp, -hp, hp), new Vector3d(-hp, hp, hp)).setColor(c));
		g.add(new Line(new Vector3d(hp, -hp, hp), new Vector3d(hp, hp, hp)).setColor(c));

		g.add(new Line(new Vector3d(-hp, -hp, -hp), new Vector3d(-hp, -hp, hp)).setColor(c));
		g.add(new Line(new Vector3d(hp, -hp, -hp), new Vector3d(hp, -hp, hp)).setColor(c));
		g.add(new Line(new Vector3d(-hp, hp, -hp), new Vector3d(-hp, hp, hp)).setColor(c));
		g.add(new Line(new Vector3d(hp, hp, -hp), new Vector3d(hp, hp, hp)).setColor(c));

		g.frame.m03(position.x);
		g.frame.m13(position.y);
		g.frame.m23(position.z);

		gd.add(g);
	}
}
