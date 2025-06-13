package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

public class SDFOpSubtractConstant extends SDF {

	private double constant = 0;

	public SDFOpSubtractConstant(SDF a, double constant) {
		this.childA = a;
		this.constant = constant;

		displayName = "OpSubtractConstant";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		ad -= constant;

		return (ad);
	}


	@Override
	public Material getMaterial(Vector3d v, double time) {
		return childA.getMaterial(v, time);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		return null;
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		String compStringA = childA.getSourceRepresentation(definitions, functions, transforms, vLocalLast, time);
		
		return "(" + compStringA + " - " + constant + ")";
	}


}
