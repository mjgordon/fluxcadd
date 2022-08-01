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
	public DistanceData getDistance(Vector3d v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);

		if (aD.distance > -bD.distance) {
			return aD;
		}
		else {
			bD.distance *= -1;
			return bD;
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		a.extractSceneGeometry(gd, solid);
		b.extractSceneGeometry(gd, false);
	}
}
