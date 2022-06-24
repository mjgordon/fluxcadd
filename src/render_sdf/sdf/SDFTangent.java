package render_sdf.sdf;

import utility.PVectorD;

public class SDFTangent extends SDF {
	
	private int axis;
	private double scalar;
	
	public SDFTangent(int axis,double scalar) {
		this.axis = axis;
		this.scalar = scalar;
	}

	@Override
	public double getDistance(PVectorD v) {
		return(Math.max(Math.tan(v.array()[axis] / scalar),0.1));
	}

}
