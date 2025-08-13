package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

import utility.UtilFunctional.TriFunction;

public class SDFOpModulo extends SDF {
	
	private Vector3d stride = new Vector3d(-1, -1, -1);
	
	
	
	private TriFunction<Vector3d, Double, VectorContext, Double> childDistance = ((vec, t, context) -> childA.getDistance(vec,  t, context));


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
	public double getDistance(Vector3d v, double time, VectorContext context) {
		return distanceFunction(v, time, childDistance, stride, context);
	}
	
	
	public static double distanceFunction(Vector3d position, double time, TriFunction<Vector3d, Double, VectorContext, Double> childDistance, Vector3d stride, VectorContext context) {
		// I think this one can't be brought into the VectorContext because there may be nested modulos
		Vector3d offsetVector = new Vector3d();
		
		long idX = 0;
		long idY = 0;
		long idZ = 0;
		
		double dirX = 0;
		double dirY = 0;
		double dirZ = 0;
		
		if (stride.x > 0 ) {
			idX = Math.round(position.x / stride.x);
			dirX = Math.signum(position.x - (stride.x * idX));
		}
		
		if (stride.y > 0) {
			idY =  Math.round(position.y / stride.y);
			dirY = Math.signum(position.y - (stride.y * idY));		
		}
		
		if (stride.z > 0) {
			idZ =  Math.round(position.z / stride.z);
			dirZ = Math.signum(position.z - (stride.z * idZ));
		}

		double distance = Double.MAX_VALUE;
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					offsetVector.set(x, y, z).mul(dirX, dirY, dirZ).add(idX, idY, idZ);
					offsetVector.mul(stride);
					position.sub(offsetVector, offsetVector);
					distance = Math.min(distance, childDistance.apply(offsetVector, time, context));
				}
			}
		}

		return distance;
	}


	@Override
	public Material getMaterial(Vector3d v, double time, VectorContext context) {
		return childA.getMaterial(v, time, context);
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
		
		String vecName = "vectorStride" + compileName;
		String vecString = "private Vector3d " + vecName + " = " + getCompiledVectorString(stride);
		definitions.add(vecString);
		
		String funcName = "distanceInner" + compileName;
		String funcString = ""
				+ " private double " + funcName + "(Vector3d v, double time, VectorContext context) {\n"
				+ "  return " + compStringA + ";\n"
				+ " }\n";
		
		functions.add(funcString);
		
		String funcName2 = "childDistance" + compileName;
		String funcString2 = " private TriFunction<Vector3d, Double, VectorContext, Double> " + funcName2 + " = ((vec, t, context) -> " + funcName + "(vec, t, context));";
		functions.add(funcString2);
		
		return "SDFOpModulo.distanceFunction(" + vLocalLast + ", " + time + ", " + funcName2 + ", " + vecName + ", context)";
	}

}
