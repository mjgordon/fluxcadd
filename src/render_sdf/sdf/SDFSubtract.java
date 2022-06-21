package render_sdf.sdf;

import utility.VectorD;

public class SDFSubtract extends SDF {
	
	private SDF a;
	private SDF b;
	private double factor;
	
	public SDFSubtract(SDF a, SDF b, double factor) {
		this.a = a;
		this.b = b;
	}

	@Override
	public double getDistance(VectorD v) {
		return(a.getDistance(v) - (b.getDistance(v) * factor));
	}

}
