package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

public class SDFOpTransform extends SDF {

	private Matrix4dAnimated frame;


	public SDFOpTransform(SDF child) {
		Matrix4d base = new Matrix4d();
		this.frame = new Matrix4dAnimated(base, "Transform");

		this.childA = child;

		displayName = "OpTranslate";
	}


	public SDFOpTransform(SDF child, Vector3d vector) {
		Matrix4d base = new Matrix4d().setTranslation(vector);
		this.frame = new Matrix4dAnimated(base, "Transform");

		this.childA = child;

		displayName = "OpTranslate";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		// The 'new' is still required here, as the resulting position may be used by an arbitrary number of other objects
		Vector3d vLocal = frame.getInvert(time).transformPosition(v, new Vector3d());
		return childA.getDistance(vLocal, time, context);
	}


	@Override
	public Material getMaterial(Vector3d v, double time, VectorContext context) {
		Vector3d vLocal = frame.getInvert(time).transformPosition(v, new Vector3d());
		return childA.getMaterial(vLocal, time, context);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}


	public SDFOpTransform addKeyframe(double timestamp, Matrix4d m) {
		frame.addKeyframe(timestamp, m);
		return this;
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms,  String vLocalLast, double time) {
		
		Matrix4d matrixInvert = frame.getInvert(time);
		String matrixInvertName = "mInvert" + this.compileName;
		definitions.add("private Matrix4d " + matrixInvertName + " = " + getCompileMatrixString(matrixInvert));
		
		String vLocalNew = "v" + compileName;
		
		String vDef = "Vector3d " + vLocalNew + " = " + vLocalLast + ".mulPosition(" + matrixInvertName + ", new Vector3d());";
		transforms.add(vDef);
		
		return childA.getSourceRepresentation(definitions, functions, transforms, vLocalNew, time);
	}

}
