package render_sdf.sdf;

import utility.VectorD;

public class SDFSphere extends SDF {
	private VectorD position;
	private double radius;
	
	public SDFSphere(VectorD position,double radius) {
		this.position = position;
		this.radius = radius;
	}

	@Override
	public double getDistance(VectorD v) {
		return VectorD.dist(v, position) - radius;
	}
}
