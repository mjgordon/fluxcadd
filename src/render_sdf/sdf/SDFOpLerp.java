package render_sdf.sdf;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;
import utility.math.UtilMath;

public class SDFOpLerp extends SDF {

	private double f;


	public SDFOpLerp(SDF a, SDF b, double f) {
		this.childA = a;
		this.childB = b;
		this.f = f;

		displayName = "OpLerp";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		double ad = childA.getDistance(v, time, context);
		double bd = childB.getDistance(v, time, context);

		return UtilMath.lerp(ad, bd, f);
	}


	@Override
	public Material getMaterial(Vector3d v, double time, VectorContext context) {
		return Material.lerpMaterial(childA.getMaterial(v, time, context), childB.getMaterial(v, time, context), f);
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
		
		return "UtilMath.lerp(" + compStringA + ", " + compStringB + ", " + f + ")";
	}
	
	
	@Override
	public void getGLSLDistanceRepresentation(String positionName, LinkedHashSet<String> source, LinkedHashSet<String> predefinitions) { 
		childA.getGLSLDistanceRepresentation(positionName, source, predefinitions);
		childB.getGLSLDistanceRepresentation(positionName, source, predefinitions);
		String line = "  float dist" + this.getCompileName() + " = sdfOpLerp(dist" + childA.getCompileName() + ", dist" + childB.getCompileName() + ", " + f + ");";
		source.add(line);
	}
	

	@Override
	public void getGLSLMaterialRepresentation(String positionName, LinkedHashSet<String> source, LinkedHashSet<String> materialPredefinitions) {
		childA.getGLSLMaterialRepresentation(positionName, source, materialPredefinitions);
		childB.getGLSLMaterialRepresentation(positionName, source, materialPredefinitions);
		
		childA.getGLSLDistanceRepresentation(positionName, source, new LinkedHashSet<String>());
		childB.getGLSLDistanceRepresentation(positionName, source, new LinkedHashSet<String>());
		
		source.add("  Material mat" + this.getCompileName() + " = materialOpLerp(mat" + childA.getCompileName() + ", mat" + childB.getCompileName() + ", " + f + ");");
	}

}
