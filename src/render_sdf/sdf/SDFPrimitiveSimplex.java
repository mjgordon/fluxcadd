package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.SimplexNoise;
import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

public class SDFPrimitiveSimplex extends SDF {

	
	private double scale;
	
	SimplexNoise simplex;


	public SDFPrimitiveSimplex(Material material, double scale) {
		
		this.simplex = new SimplexNoise();
		this.material = material;
		this.scale = scale;

		displayName = "PrimSimplex";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		return SimplexNoise.noise((float)(v.x * scale), (float)(v.y * scale), (float)(v.z * scale), (float)time) * 0.5 + 0.5;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
	}


	@Override
	public Animated[] getAnimated() {
		return null;
	}
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		String out = "(SimplexNoise.noise((float)(" + vLocalLast + ".x * " + scale + "), (float)(" + vLocalLast + ".y * " + scale + "), (float)(" + vLocalLast + ".z * " + scale + "),(float) time) * 0.5 + 0.5)";
		return out;
	}

}
