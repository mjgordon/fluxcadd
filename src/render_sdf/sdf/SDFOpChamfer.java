package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.material.Material;

public class SDFOpChamfer extends SDF {
	private SDF a;
	private SDF b;
	private float size;


	public SDFOpChamfer(SDF a, SDF b, float size) {
		this.a = a;
		this.b = b;
		this.size = size;
	}


	@Override
	public DistanceData getDistance(Vector3d v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);
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
			aD.distance = distC * 0.1;
			double factor = distA / (distA + distB);
			aD.material = Material.lerpMaterial(aD.material, bD.material, factor);
			return (aD);
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		a.extractSceneGeometry(gd, solid);
		b.extractSceneGeometry(gd, solid);

	}
}
