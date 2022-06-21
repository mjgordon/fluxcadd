package render_sdf.raymarcher;

import static java.lang.Math.abs;

import utility.VectorD;

public class SDFDiamond extends SDF {
	private VectorD position;
	private double size;

	public SDFDiamond(VectorD position, float size) {
		this.position = position;
		this.size = size;
	}

	@Override
	public double getDistance(VectorD v) {
		VectorD dist = VectorD.sub(position,v);

		return (abs(dist.x) + abs(dist.y) + abs(dist.z)
				- size);

	}

}
