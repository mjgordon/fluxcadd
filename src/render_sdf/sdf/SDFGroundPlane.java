package render_sdf.sdf;

import utility.PVectorD;

public class SDFGroundPlane extends SDF {
	private double height;
	
	public SDFGroundPlane(float height) {
		this.height = height;
	}

	@Override
	public double getDistance(PVectorD v) {
		return(v.z - height);
	}
	
	
}
