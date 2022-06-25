package render_sdf.sdf;

import utility.PVectorD;

public class SDFPrimitiveSphere extends SDF {
	private PVectorD position;
	private double radius;
	
	public SDFPrimitiveSphere(PVectorD position,double radius) {
		this.position = position;
		this.radius = radius;
	}

	@Override
	public double getDistance(PVectorD v) {
		return PVectorD.dist(v, position) - radius;
	}
}
