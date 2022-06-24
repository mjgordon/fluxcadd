package render_sdf.raytracer;

import render_sdf.material.Material;
import utility.Color;
import utility.PVectorD;

public class GroundPlane extends RenderGeometry {
	public double zHeight;

	public GroundPlane(double zHeight) {
		this.zHeight = zHeight;
		this.material = new Material(new Color(0xFF5555AA),0);
	}

	public PVectorD intersect(PVectorD origin, PVectorD direction) {
		double n = origin.z / -direction.z;
		if (n < 0) {
			return (null);
		}

		PVectorD output = PVectorD.add(origin, PVectorD.mult(direction, n));
		output.z = 0.001f;
		return (output);
	}

	public PVectorD getNormal(PVectorD input) {
		return (new PVectorD(0, 0, 1));
	}
}