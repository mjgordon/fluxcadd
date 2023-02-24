package render_sdf.sdf;

import geometry.Geometry;
import geometry.GeometryDatabase;
import render_sdf.material.Material;
import utility.Color;

import org.joml.Vector3d;

public abstract class SDF {

	/**
	 * Distance at which a ray is considered touching
	 */
	public static final double epsilon = 0.000001;
	
	/**
	 * Factor to reduce the actual moved distance compared to the calculated distance
	 */
	public static final double distanceFactor = 0.99;

	public Material material = null;

	public Geometry previewGeometry = null;

	protected static final Color previewColorSolid = new Color(0, 255, 255);
	protected static final Color previewColorVoid = new Color(255, 127, 0);
	
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


	public abstract DistanceData getDistance(Vector3d v, double time);

	public abstract void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview);
	
	protected String displayName = "UNSET";


	public Vector3d getNormal(Vector3d v, double time) {
		if (extraNormal) {
			double a = getDistance(new Vector3d(v.x + epsilon, v.y, v.z), time).distance - getDistance(new Vector3d(v.x - epsilon, v.y, v.z), time).distance;
			double b = getDistance(new Vector3d(v.x, v.y + epsilon, v.z), time).distance - getDistance(new Vector3d(v.x, v.y - epsilon, v.z), time).distance;
			double c = getDistance(new Vector3d(v.x, v.y, v.z + epsilon), time).distance - getDistance(new Vector3d(v.x, v.y, v.z - epsilon), time).distance;

			Vector3d out = new Vector3d(a, b, c).normalize();

			return (out);
		}
		else {
			double d = getDistance(v, time).distance;
			double a = getDistance(new Vector3d(v.x + epsilon, v.y, v.z), time).distance - d;
			double b = getDistance(new Vector3d(v.x, v.y + epsilon, v.z), time).distance - d;
			double c = getDistance(new Vector3d(v.x, v.y, v.z + epsilon), time).distance - d;

			Vector3d out = new Vector3d(a, b, c).normalize();

			return (out);
		}
	}


	public Color getPrimitiveColor(boolean solid, boolean materialPreview) {
		return materialPreview ? this.material.diffuseColor : (solid ? previewColorSolid : previewColorVoid);
	}
	
	public String describeTree(String input, int depth, String prefix, boolean last) {
		input += "\n";
		//input += prefix;
		if (depth > 0) {
			//System.out.println(displayName + "//" + prefix + "//" + prefix.substring(0,prefix.length() - 1));
			input += prefix.substring(0,prefix.length() - 1) + ((last) ? PIPE_ELBOW : PIPE_TEE);
			//input += (last) ? PIPE_ELBOW : PIPE_TEE;
		}
		input += displayName;
		
		System.out.println(input);
		System.out.println("");
		
		return input;
	}
}
