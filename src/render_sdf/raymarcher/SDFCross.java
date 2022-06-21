package render_sdf.raymarcher;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import utility.VectorD;

import static java.lang.Math.max;

public class SDFCross extends SDF {
	private VectorD position;
	private float size;

	public SDFCross(VectorD position, float size) {
		this.position = position;
		this.size = size;
	}

	@Override
	public double getDistance(VectorD v) {
		
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);

		//return (min(min(abs(dist.x), abs(dist.y)), abs(dist.z)) 
		//		- size);
		
		return(min(min(ax + ay,ay + az),ax + az) - size);

	}

}
