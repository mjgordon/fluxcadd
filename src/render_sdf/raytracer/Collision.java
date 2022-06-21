package render_sdf.raytracer;

import utility.VectorD;

public class Collision {
	public RenderGeometry geometry;
	public VectorD position;

	public Collision(RenderGeometry geometry, VectorD position) {
		this.geometry = geometry;
		this.position = position;
	}
}