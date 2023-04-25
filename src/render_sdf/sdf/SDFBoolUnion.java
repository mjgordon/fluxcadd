package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;


public class SDFBoolUnion extends SDF {
	
	public SDFBoolUnion(SDF a, SDF b) {
		this.childA = a;
		this.childB = b;
		
		displayName = "BoolUnion";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = childA.getDistance(v, time);
		DistanceData bD = childB.getDistance(v, time);
		
		if (aD.distance < bD.distance) {
			return aD;
		}
		else {
			return bD;
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
		childB.extractSceneGeometry(gd, solid, materialPreview, time);
	}
	
}
