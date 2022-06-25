package render_sdf.sdf;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import render_sdf.material.Material;
import utility.PVectorD;


public class SDFPrimitiveCross extends SDF {
	private PVectorD position;
	private float size;

	
	public SDFPrimitiveCross(PVectorD position, float size, Material material) {
		this.position = position;
		this.size = size;
		this.material = material;
	}

	
	@Override
	public DistanceData getDistance(PVectorD v) {	
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);
		
		return(new DistanceData(min(min(ax + ay,ay + az),ax + az) - size, this.material));
	}
}
