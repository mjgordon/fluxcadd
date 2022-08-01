package render_sdf.raytracer;

import org.joml.Vector3d;

public class Collision {
	public RenderGeometry geometry;
	public Vector3d position;


	public Collision(RenderGeometry geometry, Vector3d position) {
		this.geometry = geometry;
		this.position = position;
	}
}