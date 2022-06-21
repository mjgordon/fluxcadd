package render_sdf.raymarcher;

import utility.VectorD;

public class SDFTangent extends SDF {
	
	private int axis;
	private double scalar;
	
	public SDFTangent(int axis,double scalar) {
		this.axis = axis;
		this.scalar = scalar;
	}

	@Override
	public double getDistance(VectorD v) {
		return(Math.max(Math.tan(v.array()[axis] / scalar),0.1));
	}

}
