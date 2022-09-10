package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.material.Material;
import utility.OpenSimplexNoise;

public class SDFPrimitiveSimplex extends SDF{
	
	OpenSimplexNoise simplex;
	
	public SDFPrimitiveSimplex(Material material) {
		simplex = new OpenSimplexNoise();
		
		this.material = material;
	}

	@Override
	public DistanceData getDistance(Vector3d v) {
		double s = 0.05;
		return(new DistanceData(simplex.eval(v.x * s, v.y * s,v.z * s) * 0.5 + 0.5,this.material));
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
	}

}
