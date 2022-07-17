package render_sdf.sdf;


import static java.lang.Math.abs;

import render_sdf.material.Material;
import utility.PVectorD;


/**
 * this is no longer a mystery now that the issue with SDFOpFillet was figured out (x*y creates a non-linear distance function). Not clear if it will be useful in the future. 
 * @author mattj
 *
 */
public class SDFPrimitiveFuckedStar extends SDF {
	private PVectorD position;
	private double size;
	
	public SDFPrimitiveFuckedStar(PVectorD position, double size, Material material) {
		this.position = position;
		this.size = size;
		
		this.material = material;
	}

	
	@Override
	public DistanceData getDistance(PVectorD v) {
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);
		
		return(new DistanceData( Math.max((ax * ay) - size,0.0001001), this.material));  
	}

}
