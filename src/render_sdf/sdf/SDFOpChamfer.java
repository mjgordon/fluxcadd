package render_sdf.sdf;

import utility.PVectorD;

public class SDFOpChamfer extends SDF {
	
	private SDF a;
	private SDF b;
	private float size;
	
	public SDFOpChamfer(SDF a, SDF b, float size) {
		this.a = a;
		this.b = b;
		this.size = size;
	}

	@Override
	public DistanceData getDistance(PVectorD v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);
		double distA = aD.distance;
		double distB = bD.distance;
	
		double dist = Math.min(distA + distB - size, Math.min(distA, distB));
		
		aD.distance = dist;
	
		return(aD);
	}

}
