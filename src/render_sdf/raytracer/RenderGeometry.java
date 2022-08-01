package render_sdf.raytracer;

import org.joml.Vector3d;

import render_sdf.material.Material;

public abstract class RenderGeometry {
	public Material material;


	public abstract Vector3d intersect(Vector3d origin, Vector3d direction);

	public abstract Vector3d getNormal(Vector3d input);
}
