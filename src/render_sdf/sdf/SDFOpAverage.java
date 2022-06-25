package render_sdf.sdf;

import utility.PVectorD;

public class SDFOpAverage extends SDF {
	
	private SDF a;
	private SDF b;
	
	
	public SDFOpAverage(SDF a, SDF b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public DistanceData getDistance(PVectorD v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);
		
		aD.distance = (aD.distance + bD.distance / 2.0);
		
		return(aD);
	}

}
