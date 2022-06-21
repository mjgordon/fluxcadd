package render_sdf.raymarcher;

import static java.lang.Math.abs;

import utility.VectorD;

public class SDFStar extends SDF {
	private VectorD position;
	private float size;
	
	public SDFStar(VectorD position, float size) {
		this.position = position;
		this.size = size;
	}

	@Override
	public double getDistance(VectorD v) {
		
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);
		
		return((ax * ay * az) + (ax + ay + az ) - size);  	
	}

}
