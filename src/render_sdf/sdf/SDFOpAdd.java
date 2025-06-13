package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

public class SDFOpAdd extends SDF {

	private double mult;


	public SDFOpAdd(SDF a, SDF b, double mult) {
		this.childA = a;
		this.childB = b;
		this.mult = mult;

		displayName = "OpAdd";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);

		return ad + (bd * mult);
	}


	// TODO : Check where this is used and if it needs a different transition
	@Override
	public Material getMaterial(Vector3d v, double time) {
		return childA.getMaterial(v, time);
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
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		String compStringA = childA.getSourceRepresentation(definitions, functions, transforms, vLocalLast, time);
		String compStringB = childB.getSourceRepresentation(definitions, functions, transforms, vLocalLast, time);
		
		return "(" + compStringA + " + (" + compStringB + " * " + mult + "))";
	}

}
