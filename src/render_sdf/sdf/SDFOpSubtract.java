package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

public class SDFOpSubtract extends SDF {

	private double factor;


	public SDFOpSubtract(SDF a, SDF b, double factor) {
		this.childA = a;
		this.childB = b;
		this.factor = factor;

		displayName = "OpSubtract";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		double ad = childA.getDistance(v, time, context);
		double bd = childB.getDistance(v, time, context);

		return ad - (bd * factor);
	}


	@Override
	public Material getMaterial(Vector3d v, double time, VectorContext context) {
		return childA.getMaterial(v, time, context);
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
		
		return "(" + compStringA + " - (" + compStringB + " * " + factor + "))";
	}


	@Override
	public void getGLSLRepresentation(String positionName, ArrayList<String> source) { 
		childA.getGLSLRepresentation(positionName, source);
		childB.getGLSLRepresentation(positionName, source);
		source.add("  float dist" + this.compileName + " = sdfOpSubtract(dist" + childA.compileName + ", dist" + childB.compileName + ", " + factor + ");");
	}

}
