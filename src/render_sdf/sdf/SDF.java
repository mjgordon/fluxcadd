package render_sdf.sdf;

import render_sdf.material.Material;
import utility.PVectorD;

public abstract class SDF {
	
	public static double epsilon = 0.00001;
	public static double distanceFactor = 0.5;
			
	public Material material = null;
	
	public abstract DistanceData getDistance(PVectorD v);
	
	public PVectorD getNormal(PVectorD v) {
		double a = getDistance(new PVectorD(v.x + epsilon,v.y,v.z)).distance - getDistance(new PVectorD(v.x - epsilon,v.y,v.z)).distance;
		double b = getDistance(new PVectorD(v.x,v.y + epsilon,v.z)).distance - getDistance(new PVectorD(v.x,v.y - epsilon,v.z)).distance;
		double c = getDistance(new PVectorD(v.x,v.y,v.z + epsilon)).distance - getDistance(new PVectorD(v.x,v.y,v.z - epsilon)).distance;
		
		PVectorD out = new PVectorD(a,b,c);
		return(out.normalize());
	}
}
