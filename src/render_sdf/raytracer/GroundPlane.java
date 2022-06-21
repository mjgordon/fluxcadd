package render_sdf.raytracer;

import render_sdf.material.Material;
import utility.Color;
import utility.VectorD;

public class GroundPlane extends RenderGeometry {
	public double zHeight;

	public GroundPlane(double zHeight) {
		this.zHeight = zHeight;
		this.material = new Material(new Color(0xFF5555AA),0);
	}

	public VectorD intersect(VectorD origin, VectorD direction) {
		double n = origin.z / -direction.z;
		if (n < 0) {
			return (null);
		}

		VectorD output = VectorD.add(origin, VectorD.mult(direction, n));
		output.z = 0.001f;
		return (output);
	}

	public VectorD getNormal(VectorD input) {
		return (new VectorD(0, 0, 1));
	}
}