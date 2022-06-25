package render_sdf.sdf;

import static java.lang.Math.abs;

import render_sdf.material.Material;
import utility.PVectorD;

public class SDFPrimitiveDiamond extends SDF {
	private PVectorD position;
	private double size;


	public SDFPrimitiveDiamond(PVectorD position, float size, Material material) {
		this.position = position;
		this.size = size;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(PVectorD v) {
		PVectorD dist = PVectorD.sub(position, v);

		return (new DistanceData(abs(dist.x) + abs(dist.y) + abs(dist.z) - size, this.material));

	}

}
