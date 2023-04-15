package render_sdf.animation;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector4d;

public class Matrix4dAnimated {
	private double[] timeStamps;
	private Matrix4d[] matrixPositions;
	
	private double cachedTime = Double.NaN;
	
	private Matrix4d cachedMatrix = null;
	private Matrix4d cachedMatrixInvert = null;
	
	private double[] cachedArray;
	private double[] cachedArrayInvert;
	
	public Matrix4dAnimated(Matrix4d base) {
		timeStamps = new double[1];
		timeStamps[0] = 0;
		
		matrixPositions = new Matrix4d[1];
		matrixPositions[0] = base;
		
		cachedArray = new double[16];
		cachedArrayInvert = new double[16];
		for (int i = 0; i < 16; i++) {	
			cachedArray[i] = 0;
			cachedArrayInvert[i] = 0;
		}
	}
	
	public Matrix4d get(double time) {
		ensure(time);
		return cachedMatrix;
	}
	
	public Matrix4d getInvert(double time) {
		ensure(time);
		return cachedMatrixInvert;
	}
	
	public double[] getArray(double time) {
		ensure(time);
		return(cachedArray);
	}
	
	public double[] getArrayInvert(double time) {
		ensure(time);
		return(cachedArrayInvert);
	}
	
	// TODO: Find better name
	private void ensure(double time) {
		if (cachedMatrixInvert == null || time != cachedTime) {
			recalculate(time);
		}
	}
	
	/**
	 * Calculates the lerped Matrix at the requested time. 
	 * As this AnimatedMatrix will be referenced many times in a single frame, this is cached so should only be recalculated once per frame
	 * For now this is a straight lerp between keyframes, we can look into splines/accel later
	 * @param time
	 */
	private void recalculate(double time) {
		// Requested time is before keyframes
		if (time <= timeStamps[0]) {
			cachedMatrix = matrixPositions[0];
		}
		// Requested time is after keyframes
		else if (time >= timeStamps[timeStamps.length - 1]) {
			cachedMatrix = matrixPositions[timeStamps.length - 1];
		}
		// Requested time is within keyframes
		else {
			int idA = -1;
			int idB = -1;
			for (int i = 0; i < timeStamps.length; i++) {
				if (time >= timeStamps[i]) {
					idA = i;
					idB = i + 1;
					break;
				}
			}
			if (idA == -1) {
				System.out.println("Error: bad time value requested from AnimatedMatrix4d");
			}
			
			double timeNormalized = (time - timeStamps[idA]) / (timeStamps[idB] - timeStamps[idA]);
			
			Vector4d posA = matrixPositions[idA].getColumn(3, new Vector4d());
			Vector4d posB = matrixPositions[idB].getColumn(3, new Vector4d());
			
			// Normalized vs unnormalized?
			Quaterniond quatA = new Quaterniond().setFromNormalized(matrixPositions[idA]);
			Quaterniond quatB = new Quaterniond().setFromNormalized(matrixPositions[idB]);
			
			Vector4d posNew = posA.lerp(posB, timeNormalized, new Vector4d());
			Quaterniond quatNew = quatA.slerp(quatB, timeNormalized, new Quaterniond());
			
			cachedMatrix = new Matrix4d();
			quatNew.get(cachedMatrix);
			cachedMatrix.setColumn(3, posNew);
		}
		
		cachedMatrixInvert = new Matrix4d(cachedMatrix).invert();
		
		cachedMatrix.get(cachedArray);
		cachedMatrixInvert.get(cachedArrayInvert);
	}
}
