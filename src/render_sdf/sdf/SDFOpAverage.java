package render_sdf.sdf;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

public class SDFOpAverage extends SDF {

	public SDFOpAverage(SDF a, SDF b) {
		this.childA = a;
		this.childB = b;

		displayName = "OpAverage";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		double ad = childA.getDistance(v, time, context);
		double bd = childB.getDistance(v, time, context);

		return (ad + bd) * 0.5;
	}


	// TODO : The lerp here still needs testing
	@Override
	public Material getMaterial(Vector3d v, double time, VectorContext context) {
		double ad = childA.getDistance(v, time, context);
		double bd = childB.getDistance(v, time, context);

		return Material.lerpMaterial(childA.getMaterial(v, time, context), childB.getMaterial(v, time, context), 1 - (ad / (ad + bd)));
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
		
		return "((" + compStringA + " + " + compStringB + " ) * 0.5)";
	}
	
	
	@Override
	public void getGLSLDistanceRepresentation(String positionName, LinkedHashSet<String> source, LinkedHashSet<String> predefinitions) { 
		childA.getGLSLDistanceRepresentation(positionName, source, predefinitions);
		childB.getGLSLDistanceRepresentation(positionName, source, predefinitions);
		String line = "  float dist" + this.getCompileName() + " = sdfOpAverage(dist" + childA.getCompileName() + ", dist" + childB.getCompileName() + ");";
		source.add(line);
	}
	
	
	@Override
	public void getGLSLMaterialRepresentation(String positionName, LinkedHashSet<String> source, LinkedHashSet<String> materialPredefinitions) {
		childA.getGLSLMaterialRepresentation(positionName, source, materialPredefinitions);
		childB.getGLSLMaterialRepresentation(positionName, source, materialPredefinitions);
		
		childA.getGLSLDistanceRepresentation(positionName, source, new LinkedHashSet<String>());
		childB.getGLSLDistanceRepresentation(positionName, source, new LinkedHashSet<String>());
		
		source.add("  Material mat" + this.getCompileName() + " = materialOpAverage(mat" + childA.getCompileName() + ", mat" + childB.getCompileName() + ", dist" + childA.getCompileName() + ", dist" + childB.getCompileName() + ");");
	}


}
