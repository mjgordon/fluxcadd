package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;


public class SDFBoolDifference extends SDF {
	private SDF a;
	private SDF b;


	public SDFBoolDifference(SDF a, SDF b) {
		this.a = a;
		this.b = b;
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = a.getDistance(v, time);
		DistanceData bD = b.getDistance(v, time);

		if (aD.distance > -bD.distance) {
			return aD;
		}
		else {
			bD.distance *= -1;
			return bD;
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		a.extractSceneGeometry(gd, solid, materialPreview);
		b.extractSceneGeometry(gd, false, materialPreview);
	}


	@Override
	public String describeTree(String input, int depth) {
		input += "\n";
		input += " ".repeat(depth);
		input += "BoolDifference";
		input = a.describeTree(input, depth + 1);
		input = b.describeTree(input, depth + 1);
		return input;
	}
}
