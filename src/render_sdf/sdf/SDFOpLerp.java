package render_sdf.sdf;


import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import utility.math.UtilMath;

public class SDFOpLerp extends SDF {
	private double f;


	public SDFOpLerp(SDF a, SDF b, double f) {
		this.childA = a;
		this.childB = b;
		this.f = f;
		
		displayName = "OpLerp";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = childA.getDistance(v, time);
		DistanceData bD = childB.getDistance(v, time);

		double dist = UtilMath.lerp(aD.distance, bD.distance, f);

		aD.distance = dist;

		return (aD);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
		childB.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
