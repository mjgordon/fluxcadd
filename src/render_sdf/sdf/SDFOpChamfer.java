package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.material.Material;

public class SDFOpChamfer extends SDF {
	private SDF a;
	private SDF b;
	private double size;


	public SDFOpChamfer(SDF a, SDF b, double size) {
		this.a = a;
		this.b = b;
		this.size = size;
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = a.getDistance(v, time);
		DistanceData bD = b.getDistance(v, time);
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
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		a.extractSceneGeometry(gd, solid, materialPreview);
		b.extractSceneGeometry(gd, solid, materialPreview);

	}
	
	@Override
	public String describeTree(String input, int depth, String spacer) {
		input = super.describeTree(input, depth, spacer);
		input += "OpChamfer";
		input = a.describeTree(input, depth + 1, PIPE_TEE);
		input = b.describeTree(input, depth + 1, PIPE_ELBOW);
		return input;
	}
}
