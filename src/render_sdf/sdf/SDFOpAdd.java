package render_sdf.sdf;


import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;


public class SDFOpAdd extends SDF {
	private double mult;
	
	
	public SDFOpAdd(SDF a, SDF b,double mult) {
		this.childA = a;
		this.childB = b;
		this.mult = mult;
		
		displayName = "BoolAdd";
	}

	
	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = childA.getDistance(v, time );
		DistanceData bD = childB.getDistance(v, time);
		
		aD.distance += (bD.distance * mult);
		
		return(aD);
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
