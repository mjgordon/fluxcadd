package render_sdf.raytracer;

import utility.VectorD;

public class Collision {
	public Geometry geometry;
	public VectorD position;

	public Collision(Geometry geometry, VectorD position) {
		this.geometry = geometry;
		this.position = position;
	}
}