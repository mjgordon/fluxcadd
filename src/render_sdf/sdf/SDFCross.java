package render_sdf.sdf;

import static java.lang.Math.abs;
import static java.lang.Math.min;

import utility.PVectorD;

import static java.lang.Math.max;

public class SDFCross extends SDF {
	private PVectorD position;
	private float size;

	public SDFCross(PVectorD position, float size) {
		this.position = position;
		this.size = size;
	}

	@Override
	public double getDistance(PVectorD v) {
		
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);
		
		return(min(min(ax + ay,ay + az),ax + az) - size);

	}

}
