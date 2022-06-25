package render_sdf.sdf;

import utility.PVectorD;

public class SDFBoolUnion extends SDF {
	private SDF a = null;
	private SDF b = null;
	
	private SDF[] list = null;
	
	public SDFBoolUnion(SDF a, SDF b) {
		this.a = a;
		this.b = b;
	}
	
	public SDFBoolUnion(SDF[] input) {
		this.list = input;
	}

	@Override
	public double getDistance(PVectorD v) {
		if (list == null) {
			return (Math.min(a.getDistance(v), b.getDistance(v)));
		}
		else {
			double minDist = Double.MAX_VALUE;
			
			for (int i = 0; i < list.length; i++) {
				double testDist = list[i].getDistance(v);
				if (testDist < minDist) {
					minDist = testDist;
				}
			}
			return(minDist);
		}
		
	}
	
	
}
