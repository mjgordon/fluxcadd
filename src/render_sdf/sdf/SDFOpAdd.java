package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;


public class SDFOpAdd extends SDF {
	private SDF a;
	private SDF b;
	private double mult;
	
	
	public SDFOpAdd(SDF a, SDF b,double mult) {
		this.a = a;
		this.b = b;
		this.mult = mult;
	}

	
	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = a.getDistance(v, time );
		DistanceData bD = b.getDistance(v, time);
		
		aD.distance += (bD.distance * mult);
		
		return(aD);
	}

	
	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		a.extractSceneGeometry(gd, solid, materialPreview);
		b.extractSceneGeometry(gd, solid, materialPreview);
	}
	
	@Override
	public String describeTree(String input, int depth) {
		input += "\n";
		input += " ".repeat(depth);
		input += "OpAdd";
		input = a.describeTree(input, depth + 1);
		input = b.describeTree(input, depth + 1);
		return input;
	}
}
