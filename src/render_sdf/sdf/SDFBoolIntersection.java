package render_sdf.sdf;


import java.util.ArrayList;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;


public class SDFBoolIntersection extends SDF {
	
	
	public SDFBoolIntersection(SDF a, SDF b) {
		this.childA = a;
		this.childB = b;
		
		displayName = "BoolIntersection";
	}

	
	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		double ad = childA.getDistance(v, time, context);
		double bd = childB.getDistance(v, time, context);
		
		return Math.max(ad, bd);
	}
	
	
	@Override
	public Material getMaterial(Vector3d v, double time, VectorContext context) {
		double ad = childA.getDistance(v, time, context);
		double bd = childB.getDistance(v, time, context);
		
		return ad > bd ? childA.getMaterial(v, time, context) : childB.getMaterial(v, time, context);
	}

	
	@Override
	public void extractSceneGeometry(GeometryDatabase gd,boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
		childB.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		return null;
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalName, double time) {
		String compStringA = childA.getSourceRepresentation(definitions, functions, transforms, vLocalName, time);
		String compStringB = childB.getSourceRepresentation(definitions, functions, transforms, vLocalName, time);
		return "Math.max(" + compStringA + ", " + compStringB + ")";
	}
	
	
	@Override
	public void getGLSLRepresentation(String positionName, ArrayList<String> source) { 
		childA.getGLSLRepresentation(positionName, source);
		childB.getGLSLRepresentation(positionName, source);
		String line = "  float dist" + this.compileName + " = sdfBooleanIntersection(dist" + childA.compileName + ", dist" + childB.compileName + ");";
		source.add(line);
	}

}
