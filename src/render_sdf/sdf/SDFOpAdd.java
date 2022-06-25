package render_sdf.sdf;

import utility.PVectorD;

public class SDFOpAdd extends SDF {
	
	private SDF a;
	private SDF b;
	private double mult;
	
	public SDFOpAdd(SDF a, SDF b,double mult) {
		this.a = a;
		this.b = b;
		this.mult = mult;
	}

	@Override
	public double getDistance(PVectorD v) {
		return(a.getDistance(v) + (b.getDistance(v) * mult));
	}

}
