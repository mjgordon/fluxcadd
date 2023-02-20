package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;


public class SDFOpAverage extends SDF {
	
	private SDF a;
	private SDF b;
	
	
	public SDFOpAverage(SDF a, SDF b) {
		this.a = a;
		this.b = b;
	}

	
	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = a.getDistance(v, time);
		DistanceData bD = b.getDistance(v, time);
		
		aD.distance = (aD.distance + bD.distance / 2.0);
		
		return(aD);
	}

	
	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		a.extractSceneGeometry(gd, solid, materialPreview);
		b.extractSceneGeometry(gd, solid, materialPreview);
	}
}
