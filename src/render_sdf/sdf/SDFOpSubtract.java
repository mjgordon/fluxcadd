package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;

public class SDFOpSubtract extends SDF {
	private SDF a;
	private SDF b;
	private double factor;
	
	private double constant = 0;


	public SDFOpSubtract(SDF a, SDF b, double factor) {
		this.a = a;
		this.b = b;
		this.factor = factor;
		
		displayName = "OpSubtract";
	}
	
	
	public SDFOpSubtract(SDF a, double constant) {
		this.a = a;
		this.constant = constant;
		
		displayName = "OpSubtract";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		if (b == null) {
			DistanceData aD = a.getDistance(v, time);
			aD.distance -= constant;
			
			return(aD);
		}
		else {
			DistanceData aD = a.getDistance(v, time);
			DistanceData bD = b.getDistance(v, time);

			aD.distance = aD.distance - (bD.distance * factor);

			return (aD);
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		a.extractSceneGeometry(gd, solid, materialPreview);
		if (b != null) {
			b.extractSceneGeometry(gd, solid, materialPreview);
		}
	}
	
	@Override
	public String describeTree(String input, int depth, String prefix, boolean last) {
		input = super.describeTree(input, depth, prefix, last);
		
		input = a.describeTree(input, depth + 1, prefix + PIPE, false);
		input = b.describeTree(input, depth + 1, prefix + " ", true);
		return input;
	}
}
