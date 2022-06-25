package render_sdf.sdf;

import static java.lang.Math.abs;

import utility.PVectorD;

public class SDFPrimitiveStar extends SDF {
	private PVectorD position;
	private float size;
	
	public SDFPrimitiveStar(PVectorD position, float size) {
		this.position = position;
		this.size = size;
	}

	@Override
	public double getDistance(PVectorD v) {
		
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);
		
		return((ax * ay * az) + (ax + ay + az ) - size);  	
	}

}
