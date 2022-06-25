package render_sdf.sdf;

import utility.PVectorD;

public class SDFBoolIntersection extends SDF {
	private SDF a;
	private SDF b;
	
	public SDFBoolIntersection(SDF a, SDF b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public DistanceData getDistance(PVectorD v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);
		
		if (aD.distance > bD.distance) {
			return aD;
		}
		else {
			return bD;
		}
	}
	
}
