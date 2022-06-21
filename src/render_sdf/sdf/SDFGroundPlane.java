package render_sdf.sdf;

import utility.VectorD;

public class SDFGroundPlane extends SDF {
	private double height;
	
	public SDFGroundPlane(float height) {
		this.height = height;
	}

	@Override
	public double getDistance(VectorD v) {
		return(v.z - height);
	}
	
	
}
