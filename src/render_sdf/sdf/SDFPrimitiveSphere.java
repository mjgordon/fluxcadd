package render_sdf.sdf;

import render_sdf.material.Material;
import utility.PVectorD;

public class SDFPrimitiveSphere extends SDF {
	private PVectorD position;
	private double radius;
	
	public SDFPrimitiveSphere(PVectorD position,double radius, Material material) {
		this.position = position;
		this.radius = radius;
		this.material = material;
	}

	@Override
	public DistanceData getDistance(PVectorD v) {
		return( new DistanceData(PVectorD.dist(v, position) - radius, this.material));
	}
}
