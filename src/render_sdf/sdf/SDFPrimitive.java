package render_sdf.sdf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;

import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;
import utility.Color3i;

/**
 * Primitives are generally simple geometric objects, and as a whole require a position and orientation in space 
 */
public abstract class SDFPrimitive extends SDF {
	/**
	 * Stores the forward transformation for the orientation of the primitive object (position and rotation). 
	 * Scale-factors are not stored here (e.g. the size of a cube) as that led to non-linear distance results. 
	 */
	protected Matrix4dAnimated frame;
	
	protected final static String prefixMatrixInvert = "mInvert";
	
	protected String compileNameMatrixInvert; 
	
	/**
	 * Material of the SDF object
	 */
	public Material material = null;
	
	
	public Material getMaterial(Vector3d vector, double time,  VectorContext context) {
		return material.getMaterial(vector, time);
	}
	
	/**
	 * Returns the given position in a normalized or 'local' position for the primitive based on its position and rotation, without allocation.  
	 * Vector3d.mulPosition() automatically checks for identity or translation matrices for simpler operataions
	 * @param v
	 * @param matrixInvert
	 * @param context
	 * @return
	 */
	public static Vector3d getVectorLocal(Vector3d v, Matrix4d matrixInvert, VectorContext context) {
		Vector3d vl = context.primitiveInternal.set(v);
		vl.mulPosition(matrixInvert);	
		return vl;
	}
	
	/**
	 * Source representation components common to all Primitives
	 * @param definitions
	 * @param time
	 */
	protected void sourceRepresentationBackground(ArrayList<String> definitions, double time) {
		definitions.add("public Matrix4d " + compileNameMatrixInvert + " = " + getCompileMatrixString(frame.getInvert(time)));
	}
	
	
	/**
	 * Primitives will return their internally held material
	 */
	@Override
	public void getGLSLMaterialRepresentation(String positionName, LinkedHashSet<String> source, LinkedHashSet<String> materialPredefinitions) {
		Vector3f colorDecimal = material.getColor().toVec3f();
		materialPredefinitions.add("const Material mat" + this.getCompileName() + " = Material(vec3(" + colorDecimal.x + ", " + colorDecimal.y + "," + colorDecimal.z + "), " + material.getReflectivity() + ");");
	}
	
	
	@Override
	public void generateCompileNames(HashSet<String> usedNames) {
		super.generateCompileNames(usedNames);
		this.compileNameMatrixInvert = prefixMatrixInvert + this.getCompileName();
	}
	
	
	/**
	 * Sets an animation keyframe for the internal orientation matrix
	 * @param timestamp
	 * @param m
	 */
	public void addKeyframe(double timestamp, Matrix4d m) {
		frame.addKeyframe(timestamp, m);
	}
	
	
	/**
	 * Sets the local transform matrix for the timestamp. 
	 * Subclasses of SDFPrimitive with additional variables to update at this step should always call this super() as well. 
	 */
	@Override
	public void updateCompiledObject(SDF target, double time) {
		try {
			 Matrix4d targetMatrixInvert = (Matrix4d)target.getClass().getDeclaredField(compileNameMatrixInvert).get(target);
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
	
	
	/**
	 * Gets the color that should be applied to the objects preview geometry
	 * @param solid
	 * @param materialPreview
	 * @return
	 */
	protected Color3i getPrimitiveColor(boolean solid, boolean materialPreview) {
		return materialPreview ? this.material.getColor() : (solid ? previewColorSolid : previewColorVoid);
	}
		
}
