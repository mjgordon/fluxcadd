package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;

public class SDFOpSubtract extends SDF {
	private double factor;
	
	private double constant = 0;


	public SDFOpSubtract(SDF a, SDF b, double factor) {
		this.childA = a;
		this.childB = b;
		this.factor = factor;
		
		displayName = "OpSubtract";
	}
	
	
	public SDFOpSubtract(SDF a, double constant) {
		this.childA = a;
		this.constant = constant;
		
		displayName = "OpSubtract";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		if (childB == null) {
			DistanceData aD = childA.getDistance(v, time);
			aD.distance -= constant;
			
			return(aD);
		}
		else {
			DistanceData aD = childA.getDistance(v, time);
			DistanceData bD = childB.getDistance(v, time);

			aD.distance = aD.distance - (bD.distance * factor);

			return (aD);
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		childA.extractSceneGeometry(gd, solid, materialPreview);
		if (childB != null) {
			childB.extractSceneGeometry(gd, solid, materialPreview);
		}
	}

}
