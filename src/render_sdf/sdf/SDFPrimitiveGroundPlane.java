package render_sdf.sdf;

import utility.PVectorD;

public class SDFPrimitiveGroundPlane extends SDF {
	private double height;
	
	public SDFPrimitiveGroundPlane(float height) {
		this.height = height;
	}

	@Override
	public double getDistance(PVectorD v) {
		return(v.z - height);
	}
	
	
}
