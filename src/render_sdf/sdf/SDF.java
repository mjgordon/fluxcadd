package render_sdf.sdf;

import utility.PVectorD;

public abstract class SDF {
	
	public static double epsilon = 0.0001;
	public static double distanceFactor = 0.5;
	
	//public static Material defaultMaterial = new Material(0xFFFF0000,127);
			
	//public Material material = defaultMaterial;
	
	public abstract double getDistance(PVectorD v);
	
	public PVectorD getNormal(PVectorD v) {
		double a = getDistance(new PVectorD(v.x + epsilon,v.y,v.z)) - getDistance(new PVectorD(v.x - epsilon,v.y,v.z));
		double b = getDistance(new PVectorD(v.x,v.y + epsilon,v.z)) - getDistance(new PVectorD(v.x,v.y - epsilon,v.z));
		double c = getDistance(new PVectorD(v.x,v.y,v.z + epsilon)) - getDistance(new PVectorD(v.x,v.y,v.z - epsilon));
		
		PVectorD out = new PVectorD(a,b,c);
		return(out.normalize());
	}
}
