package render_sdf.renderer;

import org.joml.Vector2d;
import org.joml.Vector3d;

/**
 * Each rendering thread gets a unique context object that stores vectors used in calculation, to reduce reallocation
 */
public class VectorContext {
	/**
	 * Used in the raymarching loop, usually when calculating visibility to a light source
	 */
	public Vector3d goalPointDifference = new Vector3d();
	
	/**
	 * Used when calculating the normal from a detected surface point
	 */
	public Vector3d normalOffset = new Vector3d();
	
	/**
	 * Generally used to store the local (post transformation) position when calculating primitives. Never needs to be known outside its parent object. 
	 */
	public Vector3d primitiveInternal = new Vector3d();
	
	/**
	 * Used to store an in-calculation local position when calculating primitives (e.g. Cross)
	 */
	public Vector2d primitiveInternal2d = new Vector2d();
}