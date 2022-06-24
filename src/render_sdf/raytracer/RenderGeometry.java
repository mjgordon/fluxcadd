package render_sdf.raytracer;

import render_sdf.material.Material;
import utility.PVectorD;

public abstract class RenderGeometry {
	public Material material;

	public abstract PVectorD intersect(PVectorD origin, PVectorD direction);

	public abstract PVectorD getNormal(PVectorD input);
}
