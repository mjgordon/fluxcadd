package render_sdf.raymarcher;

import utility.VectorD;

public class SDFCube extends SDF {
	private VectorD position;
	private double size;
	
	public SDFCube(VectorD position, double size) {
		this.position = position;
		this.size = size;
	}

	@Override
	public double getDistance(VectorD v) {
		VectorD dist = VectorD.sub(v, position);
		
		return( Math.max(Math.abs(dist.x), Math.max(Math.abs(dist.y), Math.abs(dist.z))) - size);
	}
}
