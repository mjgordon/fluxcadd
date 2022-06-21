package render_sdf.sdf;


import static java.lang.Math.abs;
import static java.lang.Math.max;

import utility.VectorD;



public class SDFFuckedStar extends SDF {
	private VectorD position;
	private double size;
	
	public SDFFuckedStar(VectorD position, double size) {
		this.position = position;
		this.size = size;
	}

	@Override
	public double getDistance(VectorD v) {
		
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);
		
		return( Math.max((ax * ay) - size,0.0001001));  
	}

}
