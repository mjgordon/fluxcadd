package render_sdf.sdf;

import geometry.Geometry;
import geometry.GeometryDatabase;
import render_sdf.material.Material;
import utility.Color;
import utility.PVectorD;

public abstract class SDF {
	
	public static final double epsilon = 0.000001;
	public static final double distanceFactor = 0.5;
			
	public Material material = null;
	
	public abstract DistanceData getDistance(PVectorD v);
	
	public Geometry previewGeometry = null;
	
	protected static final Color previewColorSolid = new Color(0,0,255);
	protected static final Color previewColorVoid = new Color(255,0,0);
	
	
	/**
	 * If true, normal calculation will check in both directions on each axis
	 * If false, will only check on the positive direction from the vector position on each axis
	 * May not matter
	 */
	private boolean extraNormal = false;
	
	public PVectorD getNormal(PVectorD v) {
		if (extraNormal) {
			double a = getDistance(new PVectorD(v.x + epsilon,v.y,v.z)).distance - getDistance(new PVectorD(v.x - epsilon,v.y,v.z)).distance;
			double b = getDistance(new PVectorD(v.x,v.y + epsilon,v.z)).distance - getDistance(new PVectorD(v.x,v.y - epsilon,v.z)).distance;
			double c = getDistance(new PVectorD(v.x,v.y,v.z + epsilon)).distance - getDistance(new PVectorD(v.x,v.y,v.z - epsilon)).distance;
			
			PVectorD out = new PVectorD(a,b,c);
			return(out.normalize());	
		}
		else {
			double d = getDistance(v).distance;
			double a = getDistance(new PVectorD(v.x + epsilon,v.y,v.z)).distance - d;
			double b = getDistance(new PVectorD(v.x,v.y + epsilon,v.z)).distance - d;
			double c = getDistance(new PVectorD(v.x,v.y,v.z + epsilon)).distance - d;
			
			PVectorD out = new PVectorD(a,b,c);
			return(out.normalize());	
		}	
	}
	
	public abstract void extractSceneGeometry(GeometryDatabase gd,boolean solid);
}
