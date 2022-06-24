package render_sdf.sdf;

import utility.PVectorD;

public class SDFAdd extends SDF {
	
	private SDF a;
	private SDF b;
	private double mult;
	
	public SDFAdd(SDF a, SDF b,double mult) {
		this.a = a;
		this.b = b;
		this.mult = mult;
	}

	@Override
	public double getDistance(PVectorD v) {
		return(a.getDistance(v) + (b.getDistance(v) * mult));
	}

}
