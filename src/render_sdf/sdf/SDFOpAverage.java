package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

public class SDFOpAverage extends SDF {

	public SDFOpAverage(SDF a, SDF b) {
		this.childA = a;
		this.childB = b;

		displayName = "OpAverage";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);

		return (ad + bd) * 0.5;
	}


	// TODO : The lerp here still needs testing
	@Override
	public Material getMaterial(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);

		return Material.lerpMaterial(childA.getMaterial(v, time), childB.getMaterial(v, time), 1 - (ad / (ad + bd)));
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
