package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;


public class SDFOpAverage extends SDF {
	
	
	public SDFOpAverage(SDF a, SDF b) {
		this.childA = a;
		this.childB = b;
		
		displayName = "OpAverage";
	}

	
	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = childA.getDistance(v, time);
		DistanceData bD = childB.getDistance(v, time);
		
		aD.distance = (aD.distance + bD.distance / 2.0);
		
		return(aD);
	}

	
	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
		childB.extractSceneGeometry(gd, solid, materialPreview, time);
	}
	
}
