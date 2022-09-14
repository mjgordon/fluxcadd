package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;

public class SDFOpSubtract extends SDF {
	private SDF a;
	private SDF b;
	private double factor;


	public SDFOpSubtract(SDF a, SDF b, double factor) {
		this.a = a;
		this.b = b;
		this.factor = factor;
	}


	@Override
	public DistanceData getDistance(Vector3d v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);

		aD.distance = aD.distance - (bD.distance * factor);

		return (aD);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		a.extractSceneGeometry(gd, solid, materialPreview);
		b.extractSceneGeometry(gd, solid, materialPreview);
	}
}
