package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.material.Material;

public class SDFOpChamfer extends SDF {
	private double size;


	public SDFOpChamfer(SDF a, SDF b, double size) {
		this.childA = a;
		this.childB = b;
		this.size = size;
		
		displayName = "OpChamfer";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = childA.getDistance(v, time);
		DistanceData bD = childB.getDistance(v, time);
		double distA = aD.distance;
		double distB = bD.distance;

		double distC = distA + distB - size;

		if (distA < distB && distA < distC) {
			return aD;
		}
		else if (distB < distA && distB < distC) {
			return bD;
		}
		else {
			//aD.distance = distC;
			// Distance minimization applied to reduce artifacts when chamfering between non-linear objects (e.g. sphere-sphere; cube-cube seems to work fine)
			aD.distance = distC * 0.1;
			
			double factor = distA / (distA + distB);
			aD.material = Material.lerpMaterial(aD.material, bD.material, factor);
			return (aD);
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
		childB.extractSceneGeometry(gd, solid, materialPreview, time);

	}
}
