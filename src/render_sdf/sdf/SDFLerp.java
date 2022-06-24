package render_sdf.sdf;

import utility.PVectorD;
import utility.Util;

public class SDFLerp extends SDF {
	
	private SDF a;
	private SDF b;
	private double f;
	
	public SDFLerp(SDF a, SDF b, double f) {
		this.a = a;
		this.b = b;
		this.f = f;
	}

	@Override
	public double getDistance(PVectorD v) {
		return (Util.lerp(a.getDistance(v),b.getDistance(v),f));
	}

}
