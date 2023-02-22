package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;


public class SDFBoolIntersection extends SDF {
	private SDF a;
	private SDF b;
	
	
	public SDFBoolIntersection(SDF a, SDF b) {
		this.a = a;
		this.b = b;
	}

	
	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = a.getDistance(v, time);
		
		
		if (aD.distance > SDF.epsilon) {
			return aD;
		}
		
		DistanceData bD = b.getDistance(v, time);
		
		if (aD.distance > bD.distance) {
			return aD;
		}
		else {
			return bD;
		}
	}

	
	@Override
	public void extractSceneGeometry(GeometryDatabase gd,boolean solid, boolean materialPreview) {
		a.extractSceneGeometry(gd, solid, materialPreview);
		b.extractSceneGeometry(gd, solid, materialPreview);
	}
	
	@Override
	public String describeTree(String input, int depth, String spacer) {
		input = super.describeTree(input, depth, spacer);
		input += "BoolIntersection";
		input = a.describeTree(input, depth + 1, PIPE_TEE);
		input = b.describeTree(input, depth + 1, PIPE_ELBOW);
		return input;
	}
}
