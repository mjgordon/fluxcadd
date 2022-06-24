package render_sdf.sdf;

import utility.PVectorD;

public class SDFSine extends SDF {
	
	private int axis;
	private double scalar;
	
	public SDFSine(int axis,double scalar) {
		this.axis = axis;
		this.scalar = scalar;
	}

	@Override
	public double getDistance(PVectorD v) {
		return(Math.sin(v.array()[axis] / scalar));
	}

}
