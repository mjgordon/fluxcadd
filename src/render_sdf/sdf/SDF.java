package render_sdf.sdf;

import render_sdf.material.Material;
import utility.VectorD;

public abstract class SDF {
	
	public static double epsilon = 0.0001;
	public static double distanceFactor = 0.5;
	
	//public static Material defaultMaterial = new Material(0xFFFF0000,127);
			
	//public Material material = defaultMaterial;
	
	public abstract double getDistance(VectorD v);
	
	public VectorD getNormal(VectorD v) {
		double a = getDistance(new VectorD(v.x + epsilon,v.y,v.z)) - getDistance(new VectorD(v.x - epsilon,v.y,v.z));
		double b = getDistance(new VectorD(v.x,v.y + epsilon,v.z)) - getDistance(new VectorD(v.x,v.y - epsilon,v.z));
		double c = getDistance(new VectorD(v.x,v.y,v.z + epsilon)) - getDistance(new VectorD(v.x,v.y,v.z - epsilon));
		
		VectorD out = new VectorD(a,b,c);
		return(out.normalize());
	}
}
