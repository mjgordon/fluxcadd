package render_sdf.sdf;

import geometry.GeometryDatabase;
import utility.PVectorD;

public class SDFOpAdd extends SDF {
	
	private SDF a;
	private SDF b;
	private double mult;
	
	public SDFOpAdd(SDF a, SDF b,double mult) {
		this.a = a;
		this.b = b;
		this.mult = mult;
	}

	@Override
	public DistanceData getDistance(PVectorD v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);
		
		aD.distance += (bD.distance * mult);
		
		return(aD);
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		a.extractSceneGeometry(gd, solid);
		b.extractSceneGeometry(gd, solid);
	}
}
