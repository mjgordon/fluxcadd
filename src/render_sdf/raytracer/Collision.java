package render_sdf.raytracer;

import utility.PVectorD;

public class Collision {
	public RenderGeometry geometry;
	public PVectorD position;

	public Collision(RenderGeometry geometry, PVectorD position) {
		this.geometry = geometry;
		this.position = position;
	}
}