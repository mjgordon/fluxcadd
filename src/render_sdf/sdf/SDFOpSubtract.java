package render_sdf.sdf;

import geometry.GeometryDatabase;
import utility.PVectorD;

public class SDFOpSubtract extends SDF {
	
	private SDF a;
	private SDF b;
	private double factor;
	
	public SDFOpSubtract(SDF a, SDF b, double factor) {
		this.a = a;
		this.b = b;
	}

	@Override
	public DistanceData getDistance(PVectorD v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);
		
		aD.distance = aD.distance - (bD.distance * factor);
		
		return(aD);
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		a.extractSceneGeometry(gd, solid);
		b.extractSceneGeometry(gd, solid);
	}
	
	

}
