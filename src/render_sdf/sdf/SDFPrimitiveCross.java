package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix3x2d;
import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.Axes;
import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;
import utility.Color3i;

/**
 * The cross shape extrudes a diamond shape along each axis
 */
public class SDFPrimitiveCross extends SDFPrimitive {
	private Matrix3x2d matrixInvert2d;

	private double axisSize;
	
	/**
	 * Length of the 'face' of the 2d diamond
	 */
	private double hypotSize;

	private float previewSize = 300;


	public SDFPrimitiveCross(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Cross");
		this.axisSize = size / 2;
		this.hypotSize = Math.sqrt(Math.pow(axisSize, 2) * 2);
		this.material = material;

		displayName = "PrimCross";
		
		setupMatrix();
	}


	public SDFPrimitiveCross(Matrix4d base, double size, Material material) {
		frame = new Matrix4dAnimated(base, "Cross");
		this.axisSize = size / 2;
		this.hypotSize = Math.sqrt(Math.pow(axisSize, 2) * 2);
		this.material = material;
		
		setupMatrix();
	}
	
	
	private void setupMatrix() {
		Matrix3x2d m = new Matrix3x2d();
		double n = Math.sqrt(2) * 0.5;
		m.set(n, -n, n, n, 0, axisSize);
		m.invert();
		this.matrixInvert2d = m;
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		return distanceFunction(v, frame.getInvert(time), matrixInvert2d, hypotSize, context);
	}
	
	
	public static double distanceFunction(Vector3d v, Matrix4d matrixInvert, Matrix3x2d matrixInvert2d, double hypotSize, VectorContext context) {
		Vector3d vl = getVectorLocal(v, matrixInvert, context);
		vl.absolute();

		// For this shape, the distance can be simplified to a 2D distance from a diamond centered on the origin
		Vector2d pos;
		if (vl.x <= vl.z && vl.y <= vl.z) {
			pos = context.primitiveInternal2d.set(vl.x, vl.y);
		}
		else if (vl.x <= vl.y && vl.z <= vl.y) {
			pos = context.primitiveInternal2d.set(vl.x, vl.z);
		}
		else {
			pos = context.primitiveInternal2d.set(vl.y, vl.z);
		}
		
		// This transformation moves the position into a space with the origin at the intersection of the diamond and the Y axis, 
		// with the 'face' along the x axis
		pos.mulPosition(matrixInvert2d);
		
		
		double compX = Math.min(hypotSize, Math.max(0, pos.x));
		
		double dist = pos.distance(compX, 0);
		
		dist *= Math.signum(pos.y);
		
		return dist;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		gd.add(new Axes(frame, getPrimitiveColor(solid, materialPreview), previewSize / 2.0f));
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		sourceRepresentationBackground(definitions, time);

		String nameMatrixInvert2d = "mInvert2d" + compileName;
		definitions.add("public Matrix3x2d " + nameMatrixInvert2d + " = " + getCompileMatrixString3x2(matrixInvert2d));
		
		
		String out = "SDFPrimitiveCross.distanceFunction(" + vLocalLast + ", " + compileNameMatrixInvert + ", " + nameMatrixInvert2d + ", " + hypotSize + ", context)";
		return out;
	}
	
	@Override
	public void updateCompiledObject(SDF target, double time) {
		try {
			 Matrix4d targetMatrixInvert = (Matrix4d)target.getClass().getDeclaredField("mInvert" + compileName).get(target);
			 targetMatrixInvert.set(frame.getInvert(time));
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
