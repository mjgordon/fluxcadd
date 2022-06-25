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
	public double getDistance(PVectorD v) {
		double distA = a.getDistance(v);
		double distB = b.getDistance(v);
	
		double dist = Math.min(distA + distB - size, Math.min(distA, distB));
	
		return(dist);
	}

}
