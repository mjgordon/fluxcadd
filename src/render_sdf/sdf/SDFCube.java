package render_sdf.sdf;

import utility.PVectorD;

public class SDFCube extends SDF {
	private PVectorD position;
	private double size;
	
	public SDFCube(PVectorD position, double size) {
		this.position = position;
		this.size = size;
	}

	@Override
	public double getDistance(PVectorD v) {
		PVectorD dist = PVectorD.sub(v, position);
		
		return( Math.max(Math.abs(dist.x), Math.max(Math.abs(dist.y), Math.abs(dist.z))) - size);
	}
}
