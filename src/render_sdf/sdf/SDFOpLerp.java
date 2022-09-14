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
	}


	@Override
	public DistanceData getDistance(Vector3d v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);

		double dist = UtilMath.lerp(aD.distance, bD.distance, f);

		aD.distance = dist;

		return (aD);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		a.extractSceneGeometry(gd, solid, materialPreview);
		b.extractSceneGeometry(gd, solid, materialPreview);
	}
}
