package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

/**
 * Based on the polynomial smooth method described by Inigo Quilez at:
 * https://iquilezles.org/articles/smin/
 * 
 * @author mattj
 *
 */
public class SDFOpSmooth extends SDF {

	private double size;
	private double sizeReciprocal;


	public SDFOpSmooth(SDF a, SDF b, double size) {
		this.childA = a;
		this.childB = b;
		this.size = size;
		this.sizeReciprocal = 1 / size;

		displayName = "OpSmooth";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);

		return distanceFunction(ad, bd, size, sizeReciprocal);
	}
	
	
	public static double distanceFunction(double ad, double bd, double size, double sizeReciprocal) {
		double h = Math.max(size - Math.abs(ad - bd), 0.0) * sizeReciprocal;
		return Math.min(ad, bd) - h * h * size * 0.25;
	}


	@Override
	public Material getMaterial(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);

		double h = Math.max(size - Math.abs(ad - bd), 0.0) / size;
		double cd = Math.min(ad, bd) - h * h * size * 0.25;

		if (ad <= bd && ad <= cd) {
			return childA.getMaterial(v, time);
		}
		else if (bd <= ad && bd <= cd) {
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
		return null;
	}
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions,  ArrayList<String> transforms,  String vLocalLast, double time) {
		String compStringA = childA.getSourceRepresentation(definitions, functions, transforms, vLocalLast, time);
		String compStringB = childB.getSourceRepresentation(definitions, functions, transforms, vLocalLast, time);
		
		return "SDFOpSmooth.distanceFunction(" + compStringA + ", " + compStringB + ", " + size + ")";
	}

}
