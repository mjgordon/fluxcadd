package render_sdf.sdf;


import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

public class SDFOpSubtract extends SDF {
	private double factor;
	
	private double constant = 0;


	public SDFOpSubtract(SDF a, SDF b, double factor) {
		this.childA = a;
		this.childB = b;
		this.factor = factor;
		
		displayName = "OpSubtract";
	}
	
	
	public SDFOpSubtract(SDF a, double constant) {
		this.childA = a;
		this.constant = constant;
		
		displayName = "OpSubtract";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		if (childB == null) {
			double ad = childA.getDistance(v, time);
			ad -= constant;
			
			return(ad);
		}
		else {
			double ad = childA.getDistance(v, time);
			double bd = childB.getDistance(v, time);

			return ad - (bd * factor);
		}
	}
	
	@Override
	public Material getMaterial(Vector3d v, double time) {
		return childA.getMaterial(v, time);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
		if (childB != null) {
			childB.extractSceneGeometry(gd, solid, materialPreview, time);
		}
	}


	@Override
	public Animated[] getAnimated() {
		return null;
	}

}
