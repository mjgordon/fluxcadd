package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;


public class SDFBoolDifference extends SDF {


	public SDFBoolDifference(SDF a, SDF b) {
		this.childA = a;
		this.childB = b;
		
		displayName = "BoolDifference";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);
		
		return Math.max(ad, -bd);
	}
	
	@Override 
	public Material getMaterial(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);

		if (ad > -bd) {
			return childA.getMaterial(v, time);
		}
		else {
			return childB.getMaterial(v, time);
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
		childB.extractSceneGeometry(gd, false, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		return null;
	}
	
}
