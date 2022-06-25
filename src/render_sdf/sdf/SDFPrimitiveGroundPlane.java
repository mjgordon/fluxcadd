package render_sdf.sdf;

import render_sdf.material.Material;
import utility.PVectorD;

public class SDFPrimitiveGroundPlane extends SDF {
	private double height;
	
	
	public SDFPrimitiveGroundPlane(float height, Material material) {
		this.height = height;
		this.material = material;
	}

	
	@Override
	public DistanceData getDistance(PVectorD v) {
		return(new DistanceData(v.z - height, this.material));
	}
	
	
}
