package render_sdf.sdf;


import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;
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
	public double getDistance(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);
		
		return UtilMath.lerp(ad, bd, f);
	}
	
	@Override
	public Material getMaterial(Vector3d v, double time) {
		return Material.lerpMaterial(childA.getMaterial(v, time), childB.getMaterial(v, time), f);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
		childB.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		return null;
	}

	
}
