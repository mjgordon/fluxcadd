package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
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
	}

	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		return(new DistanceData(simplex.eval(v.x * scale, v.y * scale,v.z * scale, time) * 0.5 + 0.5,this.material));
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
	}
	
	@Override
	public String describeTree(String input, int depth, String spacer) {
		input = super.describeTree(input, depth, spacer);
		input += "PrimSimplex";
		return input;
	}

}
