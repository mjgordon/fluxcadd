package render_sdf.raymarcher;

import utility.VectorD;
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
	public double getDistance(VectorD v) {
		return (Util.lerp(a.getDistance(v),b.getDistance(v),f));
	}

}
