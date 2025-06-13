package render_sdf.sdf;

import java.util.ArrayList;
import java.util.function.BiFunction;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

public class SDFOpModulo extends SDF {
	
	private Vector3d stride = new Vector3d(-1, -1, -1);
	
	private BiFunction<Vector3d, Double, Double> childDistance = ((vec, t) -> childA.getDistance(vec,  t));


	public SDFOpModulo(SDF child, double stride) {
		this.childA = child;
		
		this.stride = new Vector3d(stride, stride, stride);
		
		displayName = "OpModulo";
	}


	public SDFOpModulo(SDF child, double strideX, double strideY, double strideZ) {
		this.childA = child;
		
		this.stride = new Vector3d(strideX, strideY, strideZ);
		
		displayName = "OpModulo";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		return distanceFunction(v, time, childDistance, stride);
	}
	
	
	public static double distanceFunction(Vector3d v, double time, BiFunction<Vector3d, Double, Double> childDistance, Vector3d stride) {
		Vector3d ids = new Vector3d();
		Vector3d offsetDirection = new Vector3d();
		
		
		Vector3d local = new Vector3d(v);
		if (stride.x > 0 ) {
			ids.x = Math.round(local.x / stride.x);
			offsetDirection.x = Math.signum(local.x - (stride.x * ids.x));
		}
		
		if (stride.y > 0) {
			ids.y =  Math.round(local.y / stride.y);
			offsetDirection.y = Math.signum(local.y - (stride.y * ids.y));		
		}
		
		if (stride.z > 0) {
			ids.z =  Math.round(local.z / stride.z);
			offsetDirection.z = Math.signum(local.z - (stride.z * ids.z));
		}

		double distance = Double.MAX_VALUE;
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					Vector3d offsetVector = (new Vector3d(x, y, z)).mul(offsetDirection).add(ids);
					offsetVector.mul(stride);
					v.sub(offsetVector, offsetVector);
					distance = Math.min(distance, childDistance.apply(offsetVector, time));
				}
			}
		}

		return distance;
	}


	@Override
	public Material getMaterial(Vector3d v, double time) {
		return childA.getMaterial(v, time);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		return null;
	}
	
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		String compStringA = childA.getSourceRepresentation(definitions, functions, transforms, vLocalLast, time);
		
		String funcName = "distanceInner" + compileName;
		String funcString = ""
				+ " private double " + funcName + "(Vector3d v, double time) {\n"
				+ "  return " + compStringA + ";\n"
				+ " }\n";
		
		functions.add(funcString);
		
		String funcName2 = "childDistance" + compileName;
		String funcString2 = " private BiFunction<Vector3d, Double, Double> " + funcName2 + " = ((vec, t) -> " + funcName + "(vec, t));";
		functions.add(funcString2);
		
		return "SDFOpModulo.distanceFunction(" + vLocalLast + ", " + time + ", " + funcName2 + ", " + getCompiledVectorString(stride) + ")";
	}

}
