package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.material.Material;
import utility.OpenSimplexNoise;

public class SDFPrimitiveSimplex extends SDF{
	
	OpenSimplexNoise simplex;
	double scale;
	
	public SDFPrimitiveSimplex(Material material, double scale) {
		simplex = new OpenSimplexNoise();
		
		this.material = material;
		this.scale = scale;
	}

	@Override
	public DistanceData getDistance(Vector3d v) {
		return(new DistanceData(simplex.eval(v.x * scale, v.y * scale,v.z * scale) * 0.5 + 0.5,this.material));
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
	}

}
