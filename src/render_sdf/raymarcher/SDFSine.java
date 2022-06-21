package render_sdf.raymarcher;

import utility.VectorD;

public class SDFSine extends SDF {
	
	private int axis;
	private double scalar;
	
	public SDFSine(int axis,double scalar) {
		this.axis = axis;
		this.scalar = scalar;
	}

	@Override
	public double getDistance(VectorD v) {
		return(Math.sin(v.array()[axis] / scalar));
	}

}
