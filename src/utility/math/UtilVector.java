package utility.math;

import org.joml.Matrix4d;
import org.joml.Vector3d;

/**
 * Utilities on top of Vector3d
 *
 */
public class UtilVector {
	
	/**
	 * Get the shortest transformation matrix between two Vectors
	 * @param a
	 * @param b
	 * @return
	 */
	public static Matrix4d getTransformVecVec(Vector3d a, Vector3d b) {
		Vector3d v = new Vector3d(a).cross(b);

		double c = a.dot(b);
		
		Matrix4d vx = new Matrix4d();
		
		vx.m00(0);
		vx.m01(v.z);
		vx.m02(-v.y);
		
		vx.m10(-v.z);
		vx.m11(0);
		vx.m12(v.x);
		
		vx.m20(v.y);
	    vx.m21(-v.x);
	    vx.m22(0);
	    
	    Matrix4d vx2 = new Matrix4d(vx).mul(vx);
	    
	    vx2.scale(1 / (1 + c));
	    
	    Matrix4d output = new Matrix4d().identity();
	    output.add(vx);
	    output.add(vx2);
	    
	    return(output);
	}
}
