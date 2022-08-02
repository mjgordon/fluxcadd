package render_sdf.sdf;

import geometry.Geometry;
import geometry.GeometryDatabase;
import render_sdf.material.Material;
import utility.Color;

import org.joml.Vector3d;

public abstract class SDF {

	public static final double epsilon = 0.000001;
	public static final double distanceFactor = 0.5;

	public Material material = null;


	public abstract DistanceData getDistance(Vector3d v);


	public Geometry previewGeometry = null;

	protected static final Color previewColorSolid = new Color(0, 255,255);
	protected static final Color previewColorVoid = new Color(255,127,0);

	/**
	 * If true, normal calculation will check in both directions on each axis If
	 * false, will only check on the positive direction from the vector position on
	 * each axis May not matter
	 */
	private boolean extraNormal = false;


	public Vector3d getNormal(Vector3d v) {
		if (extraNormal) {
			double a = getDistance(new Vector3d(v.x + epsilon, v.y, v.z)).distance - getDistance(new Vector3d(v.x - epsilon, v.y, v.z)).distance;
			double b = getDistance(new Vector3d(v.x, v.y + epsilon, v.z)).distance - getDistance(new Vector3d(v.x, v.y - epsilon, v.z)).distance;
			double c = getDistance(new Vector3d(v.x, v.y, v.z + epsilon)).distance - getDistance(new Vector3d(v.x, v.y, v.z - epsilon)).distance;

			Vector3d out = new Vector3d(a, b, c).normalize();

			return (out);
		}
		else {
			double d = getDistance(v).distance;
			double a = getDistance(new Vector3d(v.x + epsilon, v.y, v.z)).distance - d;
			double b = getDistance(new Vector3d(v.x, v.y + epsilon, v.z)).distance - d;
			double c = getDistance(new Vector3d(v.x, v.y, v.z + epsilon)).distance - d;

			Vector3d out = new Vector3d(a, b, c).normalize();

			return (out);
		}
	}


	public abstract void extractSceneGeometry(GeometryDatabase gd, boolean solid);
}
