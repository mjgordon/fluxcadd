package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

public class SDFOpAddConstant extends SDF {

	private double constant;


	public SDFOpAddConstant(SDF a, double constant) {
		this.childA = a;
		this.constant = constant;

		displayName = "OpAddConstant";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		double ad = childA.getDistance(v, time, context);

		return ad + constant;
	}


	// TODO : Check where this is used and if it needs a different transition
	@Override
	public Material getMaterial(Vector3d v, double time, VectorContext context) {
		return childA.getMaterial(v, time, context);
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
		
		return "(" + compStringA + " + " + constant + ")";
	}
	
	
	@Override
	public void getGLSLRepresentation(String positionName, ArrayList<String> source) {
		childA.getGLSLRepresentation(positionName, source);
		source.add("  float dist" + this.compileName + " = sdfOpAddConstant(dist" + childA.compileName + ", " + constant + ");");
	}

}
