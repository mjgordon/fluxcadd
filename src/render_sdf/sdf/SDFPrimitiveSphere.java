package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Polyline;
import render_sdf.material.Material;
import utility.Color;
import utility.Util;

public class SDFPrimitiveSphere extends SDF {
	private Vector3d position;
	private double radius;


	public SDFPrimitiveSphere(Vector3d position, double radius, Material material) {
		this.position = position;
		this.radius = radius;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(Vector3d v) {
		return (new DistanceData(v.distance(position) - radius, this.material));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		Group g = new Group();

		int segments = 36;

		Vector3d[] verticesX = new Vector3d[segments + 1];
		Vector3d[] verticesY = new Vector3d[segments + 1];
		Vector3d[] verticesZ = new Vector3d[segments + 1];

		for (int i = 0; i <= segments; i++) {
			double n = 1.0f * i / segments * Util.TWO_PI;

			double cosVal = Math.cos(n) * radius;

			double sinVal = Math.sin(n) * radius;

			verticesX[i] = new Vector3d(0, cosVal, sinVal);
			verticesY[i] = new Vector3d(cosVal, 0, sinVal);
			verticesZ[i] = new Vector3d(cosVal, sinVal, 0);
		}

		Color c = solid ? previewColorSolid : previewColorVoid;

		g.add(new Polyline(verticesX).setColor(c));
		g.add(new Polyline(verticesY).setColor(c));
		g.add(new Polyline(verticesZ).setColor(c));

		g.frame.m03(position.x);
		g.frame.m13(position.y);
		g.frame.m23(position.z);

		gd.add(g);
	}
}
