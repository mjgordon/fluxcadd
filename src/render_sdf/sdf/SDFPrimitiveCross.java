package render_sdf.sdf;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveCross extends SDF {
	private Vector3d position;
	private float size;

	private float previewSize = 100;


	public SDFPrimitiveCross(Vector3d position, float size, Material material) {
		this.position = position;
		this.size = size;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(Vector3d v) {
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);

		return (new DistanceData(min(min(ax + ay, ay + az), ax + az) - size, this.material));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		Group g = new Group();

		float hp = previewSize / 2;

		Color c = solid ? previewColorSolid : previewColorVoid;

		g.add(new Line(new Vector3d(-hp, 0, 0), new Vector3d(hp, 0, 0)).setColor(c));
		g.add(new Line(new Vector3d(0, -hp, 0), new Vector3d(0, hp, 0)).setColor(c));
		g.add(new Line(new Vector3d(0, 0, -hp), new Vector3d(0, 0, hp)).setColor(c));
		
		g.frame.m03(position.x);
		g.frame.m13(position.y);
		g.frame.m23(position.z);

		gd.add(g);
	}
}
