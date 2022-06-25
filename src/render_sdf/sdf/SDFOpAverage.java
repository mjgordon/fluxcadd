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
	public double getDistance(PVectorD v) {
		return ( (a.getDistance(v) + b.getDistance(v)) / 2.0);
	}

}
