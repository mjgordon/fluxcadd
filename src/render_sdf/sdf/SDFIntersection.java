package render_sdf.sdf;

import utility.VectorD;

public class SDFIntersection extends SDF {
	private SDF a;
	private SDF b;
	
	public SDFIntersection(SDF a, SDF b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public double getDistance(VectorD v) {
		return (Math.max(a.getDistance(v), b.getDistance(v)));
	}
	
}
