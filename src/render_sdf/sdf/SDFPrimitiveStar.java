package render_sdf.sdf;

import static java.lang.Math.abs;

import render_sdf.material.Material;
import utility.PVectorD;

public class SDFPrimitiveStar extends SDF {
	private PVectorD position;
	private float size;
	
	
	public SDFPrimitiveStar(PVectorD position, float size, Material material) {
		this.position = position;
		this.size = size;
		this.material = material;
	}

	
	@Override
	public DistanceData getDistance(PVectorD v) {
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);
		
		return(new DistanceData((ax * ay * az) + (ax + ay + az ) - size, this.material));  	
	}

}
