package render_sdf.raytracer;

import render_sdf.material.Material;
import utility.VectorD;

public abstract class RenderGeometry {
	public Material material;

	public abstract VectorD intersect(VectorD origin, VectorD direction);

	public abstract VectorD getNormal(VectorD input);
}
