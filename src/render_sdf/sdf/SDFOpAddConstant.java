package render_sdf.sdf;

import java.util.ArrayList;
import java.util.LinkedHashSet;

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
	public void getGLSLDistanceRepresentation(String positionName, LinkedHashSet<String> source) {
		childA.getGLSLDistanceRepresentation(positionName, source);
		source.add("  float dist" + this.getCompileName() + " = sdfOpAddConstant(dist" + childA.getCompileName() + ", " + constant + ");");
	}
	
	
	@Override
	public void getGLSLMaterialRepresentation(String positionName, LinkedHashSet<String> source, LinkedHashSet<String> materialPredefinitions) {
		childA.getGLSLMaterialRepresentation(positionName, source, materialPredefinitions);
		
		source.add("  Material mat" + this.getCompileName() + " = mat" + childA.getCompileName() + ";" );
	}

}
