package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import utility.math.UtilMath;

public class SDFOpLerp extends SDF {

	private SDF a;
	private SDF b;
	private double f;


	public SDFOpLerp(SDF a, SDF b, double f) {
		this.a = a;
		this.b = b;
		this.f = f;
		
		displayName = "OpLerp";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = a.getDistance(v, time);
		DistanceData bD = b.getDistance(v, time);

		double dist = UtilMath.lerp(aD.distance, bD.distance, f);

		aD.distance = dist;

		return (aD);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		a.extractSceneGeometry(gd, solid, materialPreview);
		b.extractSceneGeometry(gd, solid, materialPreview);
	}
	
	@Override
	public String describeTree(String input, int depth, String prefix, boolean last) {
		input = super.describeTree(input, depth, prefix, last);
		
		input = a.describeTree(input, depth + 1, prefix + PIPE, false);
		input = b.describeTree(input, depth + 1, prefix + " ", true);
		return input;
	}
}
