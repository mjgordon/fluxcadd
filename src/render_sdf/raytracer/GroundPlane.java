package render_sdf.raytracer;

import org.joml.Vector3d;

import render_sdf.material.Material;
import utility.Color;

public class GroundPlane extends RenderGeometry {
	public double zHeight;

	public GroundPlane(double zHeight) {
		this.zHeight = zHeight;
		this.material = new Material(new Color(0xFF5555AA),0);
	}

	public Vector3d intersect(Vector3d origin, Vector3d direction) {
		double n = origin.z / -direction.z;
		if (n < 0) {
			return (null);
		}

		Vector3d output = new Vector3d(direction).mul(n).add(origin);
		output.z = 0.001f;
		return (output);
	}

	public Vector3d getNormal(Vector3d input) {
		return (new Vector3d(0, 0, 1));
	}
}