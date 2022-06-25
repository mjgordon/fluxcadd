package render_sdf.sdf;

import static java.lang.Math.abs;

import utility.PVectorD;

public class SDFDiamond extends SDF {
	private PVectorD position;
	private double size;


	public SDFDiamond(PVectorD position, float size) {
		this.position = position;
		this.size = size;
	}


	@Override
	public double getDistance(PVectorD v) {
		PVectorD dist = PVectorD.sub(position, v);

		return (abs(dist.x) + abs(dist.y) + abs(dist.z) - size);

	}

}
