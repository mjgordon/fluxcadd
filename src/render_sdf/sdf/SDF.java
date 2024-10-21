package render_sdf.sdf;

import geometry.Geometry;
import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;
import utility.Color3i;

import java.util.ArrayList;

import org.joml.Vector3d;

public abstract class SDF {
	
	/**
	 * First possible child SDF for boolean and op operations
	 */
	protected SDF childA = null;
	
	/**
	 * Second possible child SDF for boolean operations
	 */
	protected SDF childB = null;
	
	/**
	 * List of child SDF objects when >2 are used for boolean or op operations
	 */
	protected ArrayList<SDF> children = null;
	
	/**
	 * Material of the SDF object, if applicable (e.g. for primitives). 
	 */
	public Material material = null;

	/**
	 * Wireframe geometry displayed in the preview, if applicable
	 */
	public Geometry previewGeometry = null;

	/**
	 * Distance at which a ray is considered touching
	 */
	public static final double epsilon = 0.000001;
	
	
	/**
	 * Factor to reduce the actual moved distance compared to the calculated distance
	 */
	public static final double distanceFactor = 1.0;
	
	
	/**
	 * Maximum distance which a ray can travel before returning bg color
	 */
	public static double farClip = 5000;

	
	/**
	 * Color applied to preview geometry when object is solid
	 */
	protected static final Color3i previewColorSolid = new Color3i(0, 255, 255);
	
	
	/**
	 * Color applied to preview geometry when object is void
	 */
	protected static final Color3i previewColorVoid = new Color3i(255, 127, 0);
	
	// TODO: Move creation of nested tree strings to Util? 
	protected static final String PIPE = (char) 179 + "";
	protected static final String PIPE_TEE = (char) 195 + "";
	protected static final String PIPE_ELBOW = (char) 192 + "";

	/**
	 * If true, normal calculation will check in both directions on each axis If
	 * false, will only check on the positive direction from the vector position on
	 * each axis
	 * 
	 * May not matter
	 */
	private final boolean extraNormal = false;
	
	
	/**
	 * Name shown in the tree window
	 */
	protected String displayName = "UNSET";

	
	/**
	 * Query the SDF distance at a position and time
	 * @param vector position to query at
	 * @param time   animation time to query at
	 * @return
	 */
	public abstract double getDistance(Vector3d vector, double time);

	
	/**
	 * Adds the objects preview geometry to the supplied geometry database
	 * @param geometryDatabase
	 * @param solid
	 * @param materialPreview
	 * @param time
	 */
	public abstract void extractSceneGeometry(GeometryDatabase geometryDatabase, boolean solid, boolean materialPreview, double time);
	
	
	/**
	 * Returns all animated variables in the object for display in the timeline
	 * @return
	 */
	public abstract Animated[] getAnimated();

	
	/**
	 * Query for the local material at a position and time
	 * @param v
	 * @param time
	 * @return
	 */
	public Material getMaterial(Vector3d vector, double time) {
		return material.getMaterial(vector, time);
	}

	
	/**
	 * Query for the solid surface normal at a position and time
	 * @param v
	 * @param time
	 * @return
	 */
	public Vector3d getNormal(Vector3d v, double time) {
		if (extraNormal) {
			double a = getDistance(new Vector3d(v.x + epsilon, v.y, v.z), time) - getDistance(new Vector3d(v.x - epsilon, v.y, v.z), time);
			double b = getDistance(new Vector3d(v.x, v.y + epsilon, v.z), time) - getDistance(new Vector3d(v.x, v.y - epsilon, v.z), time);
			double c = getDistance(new Vector3d(v.x, v.y, v.z + epsilon), time) - getDistance(new Vector3d(v.x, v.y, v.z - epsilon), time);

			Vector3d out = new Vector3d(a, b, c).normalize();

			return (out);
		}
		else {
			double d = getDistance(v, time);
			double a = getDistance(new Vector3d(v.x + epsilon, v.y, v.z), time) - d;
			double b = getDistance(new Vector3d(v.x, v.y + epsilon, v.z), time) - d;
			double c = getDistance(new Vector3d(v.x, v.y, v.z + epsilon), time) - d;

			Vector3d out = new Vector3d(a, b, c).normalize();

			return (out);
		}
	}


	/**
	 * Gets the color that should be applied to the objects preview geometry
	 * @param solid
	 * @param materialPreview
	 * @return
	 */
	public Color3i getPrimitiveColor(boolean solid, boolean materialPreview) {
		return materialPreview ? this.material.getColor() : (solid ? previewColorSolid : previewColorVoid);
	}
	
	
	/**
	 * Returns a representation of the objects place in the SDF tree
	 * @param input
	 * @param depth
	 * @param prefix
	 * @param last
	 * @return
	 */
	public final String describeTree(String input, int depth, String prefix, boolean last) {	
		if (depth > 0) {
			input += "\n";
			input += prefix.substring(0,prefix.length() - 1) + ((last) ? PIPE_ELBOW : PIPE_TEE);
		}
		input += displayName;
		
		if (children == null) {
			if (childA != null) {
				if (childB != null) {
					input = childA.describeTree(input, depth + 1, prefix + PIPE, false);
					input = childB.describeTree(input, depth + 1, prefix + " ", true);
				}			
				else {
					input = childA.describeTree(input, depth + 1, prefix + " ", true);
				}
			}
		}
		else {
			for (int i = 0; i < children.size() - 1; i++) {	
				input = children.get(i).describeTree(input, depth + 1, prefix + PIPE, false);
			}
			input = children.get(children.size() - 1).describeTree(input, depth + 1, prefix + " ",  true);
		}
			
		return input;
	}
	
	
	/**
	 * Returns an SDF tree as a flat arraylist
	 * @return
	 */
	public ArrayList<SDF> getArray() {
		return getArray(new ArrayList<SDF>());
	}
	
	
	/**
	 * Returns an SDF tree as a flat arraylist
	 * @return
	 */
	private ArrayList<SDF> getArray(ArrayList<SDF> input) {
		input.add(this);
		if (childA != null) {
			input = childA.getArray(input);
		}
		if (childB != null) {
			input = childB.getArray(input);
		}
		return input;
	}
}
