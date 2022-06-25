package render_sdf.sdf;

import utility.PVectorD;
import utility.Util;

public class SDFChamfer extends SDF {
	
	private SDF a;
	private SDF b;
	private float radius;
	
	public SDFChamfer(SDF a, SDF b, float radius) {
		this.a = a;
		this.b = b;
		this.radius = radius;
	}

	@Override
	public double getDistance(PVectorD v) {
		double distA = a.getDistance(v);
		double distB = b.getDistance(v);
		
		double diff = Math.abs(distA - distB);
		
		// Wrong but interesting
		//double dist = Math.min(distA, distB) + Math.max(diff - radius,0);
		//double dist = Math.min(distA, distB) - Math.min(radius - diff,0);
		//double dist = Math.min(distA, distB) - Math.min(diff- radius,0);
		// double dist = Math.min(distA, distB) + Math.max(radius - diff,0);
		//double dist = Math.min(distA, distB) - (1 / diff);
		double dist = Math.min(distA, distB) -  Math.min(radius,(1 / diff));
		
		
		return(dist);
	}

}
