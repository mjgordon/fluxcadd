package render_sdf.sdf;

import java.util.ArrayList;
import java.util.LinkedHashSet;

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
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		
		Matrix4d matrixInvert = frame.getInvert(time);
		String matrixInvertName = "mInvert" + this.getCompileName();
		definitions.add("public Matrix4d " + matrixInvertName + " = " + getCompileMatrixString(matrixInvert));
		String vLocalNew = "v" + getCompileName();
		
		String vDef = "Vector3d " + vLocalNew + " = " + vLocalLast + ".mulPosition(" + matrixInvertName + ", new Vector3d());";
		transforms.add(vDef);
		
		return childA.getSourceRepresentation(definitions, functions, transforms, vLocalNew, time);
	}
	
	
	@Override
	public void updateCompiledObject(SDF target, double time) {
		try {
			 Matrix4d targetMatrixInvert = (Matrix4d)target.getClass().getDeclaredField("mInvert" + getCompileName()).get(target);
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


	@Override
	public void getGLSLDistanceRepresentation(String positionName, LinkedHashSet<String> source, LinkedHashSet<String> predefinitions) {
		LinkedHashSet<String> childSource = new LinkedHashSet<String>();
		String positionNameInternal = "localPosition" + this.getCompileName();
		this.childA.getGLSLDistanceRepresentation(positionNameInternal, childSource, predefinitions);
		
		String matName = this.addMat4Predefinition(this.frame.getInvert(0), predefinitions);
		
		source.add("  vec3 " + positionNameInternal + " = localPosition(" + positionName + ", " + matName + ");");
		for (String s : childSource) {
			source.add(s);
		}
		source.add("  float dist" + this.getCompileName() + " = dist" + childA.getCompileName() + ";");
	}
	
	
	@Override
	public void getGLSLMaterialRepresentation(String positionName, LinkedHashSet<String> source, LinkedHashSet<String> materialPredefinitions) {
		LinkedHashSet<String> childSource = new LinkedHashSet<String>();
		String positionNameInternal = "localPosition" + this.getCompileName();
		this.childA.getGLSLMaterialRepresentation(positionNameInternal, childSource, materialPredefinitions);

		source.add("  vec3 " + positionNameInternal + " = localPosition(" + positionName + ", mat4" + this.getCompileName() + ");");
		for (String s : childSource) {
			source.add(s);
		}
		source.add("  float mat" + this.getCompileName() + " = mat" + childA.getCompileName() + ";");
	}
	

}
