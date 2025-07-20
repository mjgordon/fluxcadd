package render_sdf.renderer;

import org.joml.Vector2d;
import org.joml.Vector3d;

/**
 * Each rendering thread gets a unique context object to reduce reallocation
 */
public class VectorContext {
	public Vector3d goalPointDifference = new Vector3d();
	public Vector3d normalOffset = new Vector3d();
	public Vector3d primitiveInternal = new Vector3d();
	public Vector2d primitiveInternal2d = new Vector2d();
}