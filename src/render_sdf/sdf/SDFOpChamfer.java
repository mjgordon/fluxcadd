package render_sdf.sdf;


import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

public class SDFOpChamfer extends SDF {
	private double size;


	public SDFOpChamfer(SDF a, SDF b, double size) {
		this.childA = a;
		this.childB = b;
		this.size = size;
		
		displayName = "OpChamfer";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);

		double cd = ad + bd - size;

		if (ad < bd && ad < cd) {
			return ad;
		}
		else if (bd < ad && bd < cd) {
			return bd;
		}
		else {
			// TODO : Minimization to reduce artificats, not a correct solution though
			return cd * 0.1;
		}
	}
	
	
	@Override
	public Material getMaterial(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);

		double cd = ad + bd - size;

		if (ad < bd && ad < cd) {
			return childA.getMaterial(v, time);
		}
		else if (bd < ad && bd < cd) {
			return childB.getMaterial(v, time);
		}
		else {
			double factor = ad / (ad + bd);
			return Material.lerpMaterial(childA.getMaterial(v, time), childB.getMaterial(v, time), factor);
		}
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
