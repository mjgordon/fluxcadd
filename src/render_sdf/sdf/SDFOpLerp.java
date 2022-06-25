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
	public double getDistance(PVectorD v) {
		return (Util.lerp(a.getDistance(v),b.getDistance(v),f));
	}

}
