package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.Box;
import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

public class SDFPrimitiveCube extends SDFPrimitive {

	private Vector3d dimensions;
	
	private static final Vector3d vectorZero = new Vector3d(0, 0, 0);

	public SDFPrimitiveCube(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		dimensions = new Vector3d(size / 2, size / 2, size / 2);

		this.frame = new Matrix4dAnimated(base, "Cube");

		this.material = material;

		displayName = "PrimCube";
	}


	public SDFPrimitiveCube(Vector3d position, double sizeX, double sizeY, double sizeZ, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		
		dimensions = new Vector3d(sizeX / 2, sizeY / 2, sizeZ / 2);

		this.frame = new Matrix4dAnimated(base, "Cube");

		this.material = material;

		displayName = "PrimCube";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		Matrix4d frameInvert = frame.getInvert(time);
		return distanceFunction(v, frameInvert, dimensions, context);
	}
	
	
	public static double distanceFunction(Vector3d v, Matrix4d frameInvert, Vector3d dimensions, VectorContext context) {
		Vector3d vl = getVectorLocal(v, frameInvert, context);
		Vector3d q = vl.absolute().sub(dimensions);
		double maxQ = Math.max(q.x, Math.max(q.y, q.z));
		double distance = q.max(vectorZero).length() + Math.min(0, maxQ);

		return distance;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		gd.add(new Box(frame));
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		sourceRepresentationBackground(definitions, time);
		
		String vectorDimsName = "vDims" + this.compileName;
		definitions.add("public Vector3d " + vectorDimsName + " = " + getCompiledVectorString(dimensions));
		
		return "SDFPrimitiveCube.distanceFunction(" + vLocalLast + ", " + compileNameMatrixInvert + ", " + vectorDimsName + ", context)";
		
	}


	@Override
	public void getGLSLRepresentation(String positionName, ArrayList<String> source) {
		String matrixInvertString = getCompiledMatrixStringGLSL(this.frame.getInvert(0));
		String dimensionsString = getCompiledVectorStringGLSL(this.dimensions);
		source.add("  float dist" + this.compileName + " = sdfPrimitiveCube(" + positionName + ", " + matrixInvertString + ", " + dimensionsString + ");");
		
	}
}
