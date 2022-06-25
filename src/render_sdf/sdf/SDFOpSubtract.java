package render_sdf.sdf;

import utility.PVectorD;

public class SDFOpSubtract extends SDF {
	
	private SDF a;
	private SDF b;
	private double factor;
	
	public SDFOpSubtract(SDF a, SDF b, double factor) {
		this.a = a;
		this.b = b;
	}

	@Override
	public double getDistance(PVectorD v) {
		return(a.getDistance(v) - (b.getDistance(v) * factor));
	}

}
