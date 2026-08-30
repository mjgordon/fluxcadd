package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.Octahedron;
import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

public class SDFPrimitiveDiamond extends SDFPrimitive {
	private double axisSize;


	public SDFPrimitiveDiamond(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Diamond");

		this.axisSize = Math.sqrt(Math.pow(size, 2) / 2);
		this.material = material;

		displayName = "PrimDiamond";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		return distanceFunction(v, frame.getInvert(time), axisSize, context);
	}
	
	
	public static double distanceFunction(Vector3d v, Matrix4d mInvert, double axisSize, VectorContext context) {
		Vector3d vl = getVectorLocal(v, mInvert, context);
		
		vl.absolute();
		
		double m = vl.x + vl.y + vl.z - axisSize;
		
		if (3 * vl.x < m) vl.set(vl.x, vl.y, vl.z);
		else if (3 * vl.x < m) vl.set(vl.y, vl.z, vl.x);
		else if (3 * vl.x < m) vl.set(vl.z, vl.x, vl.y);
		else return m * 0.57735027;
		
		double k = Math.max(0, Math.min(axisSize, 0.5 * (vl.z - vl.y + axisSize)));
		
		return Vector3d.distance(0, 0, 0, vl.x, vl.y - axisSize + k, vl.z - k);	
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		gd.add(new Octahedron(frame.get(time), getPrimitiveColor(solid, materialPreview), (float)axisSize));
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		sourceRepresentationBackground(definitions, time);
		String out = "SDFPrimitiveDiamond.distanceFunction(" + vLocalLast + ", " + compileNameMatrixInvert + ", " + axisSize +  ", context)";
		return out;
	}


	@Override
	public void getGLSLRepresentation(String positionName, ArrayList<String> source) {
		String matrixInvertString = getCompiledMatrixStringGLSL(this.frame.getInvert(0));
		source.add("  float dist" + this.compileName + " = sdfPrimitiveDiamond(" + positionName + ", " + matrixInvertString + ", " + axisSize +  ");");
		
	}

}
