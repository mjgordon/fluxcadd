package render_sdf.sdf;

import utility.PVectorD;
import utility.Util;

public class SDFOpLerp extends SDF {
	
	private SDF a;
	private SDF b;
	private double f;
	
	public SDFOpLerp(SDF a, SDF b, double f) {
		this.a = a;
		this.b = b;
		this.f = f;
	}

	@Override
	public DistanceData getDistance(PVectorD v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);
		
		double dist = Util.lerp(aD.distance, bD.distance, f);
		
		aD.distance = dist;
		
		return(aD);
	}

}
