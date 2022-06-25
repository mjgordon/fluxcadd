package render_sdf.sdf;

import render_sdf.material.Material;
import utility.PVectorD;

public class SDFPrimitiveCube extends SDF {
	private PVectorD position;
	private double size;


	public SDFPrimitiveCube(PVectorD position, double size, Material material) {
		this.position = position;
		this.size = size;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(PVectorD v) {
		PVectorD dist = PVectorD.sub(v, position);

		return (new DistanceData(Math.max(Math.abs(dist.x), Math.max(Math.abs(dist.y), Math.abs(dist.z))) - size, this.material));
	}
}
