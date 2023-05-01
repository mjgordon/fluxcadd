package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animatable;
import render_sdf.material.Material;
import utility.OpenSimplexNoise;

public class SDFPrimitiveSimplex extends SDF{
	
	OpenSimplexNoise simplex;
	double scale;
	double time = 0;
	
	public SDFPrimitiveSimplex(Material material, double scale) {
		simplex = new OpenSimplexNoise();
		
		this.material = material;
		this.scale = scale;
		
		displayName = "PrimSimplex";
	}

	@Override
	public double getDistance(Vector3d v, double time) {
		return simplex.eval(v.x * scale, v.y * scale,v.z * scale, time) * 0.5 + 0.5;
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
	}
	
	@Override
	public Animatable[] getAnimated() {
		return null;
	}
	


}
