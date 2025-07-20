package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Matrix4dc;
import org.joml.Vector3d;

import render_sdf.animation.Matrix4dAnimated;
import render_sdf.renderer.VectorContext;

public abstract class SDFPrimitive extends SDF {
	/**
	 * Stores the forward transformation for the orientation of the primitive object (position and rotation). 
	 * Scale-factors are not stored here (e.g. the size of a cube) as that led to non-linear distance results. 
	 */
	protected Matrix4dAnimated frame;
	
	/**
	 * Returns the given position in a normalized or 'local' position for the primitive based on its position and rotation, without allocation
	 * @param v
	 * @param matrixInvert
	 * @param context
	 * @return
	 */
	public static Vector3d getVectorLocal(Vector3d v, Matrix4d matrixInvert, VectorContext context) {
		Vector3d vl = context.primitiveInternal.set(v);
		
		if ((matrixInvert.properties() & Matrix4dc.PROPERTY_IDENTITY) != Matrix4dc.PROPERTY_IDENTITY) {
			vl.mulPosition(matrixInvert);	
		}
		
		return vl;
	}
	
	
	/**
	 * Sets an animation keyframe for the internal orientation matrix
	 * @param timestamp
	 * @param m
	 */
	public void addKeyframe(double timestamp, Matrix4d m) {
		frame.addKeyframe(timestamp, m);
	}
		

}
