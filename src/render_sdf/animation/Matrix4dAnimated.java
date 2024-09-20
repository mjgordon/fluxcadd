package render_sdf.animation;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector4d;


public class Matrix4dAnimated extends Animated {

	private Matrix4d[] matrixPositions;

	private Matrix4d cachedMatrix = null;
	private Matrix4d cachedMatrixInvert = null;
	private Matrix4d cachedMatrixInvertNormal = null;

	private double[] cachedArray;
	private double[] cachedArrayInvert;
	
	
	public Matrix4dAnimated(Vector3d v, String name) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(v,1));
		
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
		
		this.name = name;
	}

	public Matrix4dAnimated(Matrix4d matrix, String name) {
		timeStamps = new double[1];
		timeStamps[0] = 0;

		matrixPositions = new Matrix4d[1];
		matrixPositions[0] = new Matrix4d(matrix);

		cachedArray = new double[16];
		cachedArrayInvert = new double[16];
		for (int i = 0; i < 16; i++) {
			cachedArray[i] = 0;
			cachedArrayInvert[i] = 0;
		}
		
		this.name = name;
	}


	public void addKeyframe(double timestamp, Vector3d v) {
		addKeyframe(timestamp, new Matrix4d().setColumn(3, new Vector4d(v,1)));
	}	
	
	
	/**
	 * Adds a new keyframe, using a copy of the input matrix 
	 * @param timeStamp keyframe time
	 * @param matrix    keyframe matrix
	 */
	public void addKeyframe(double timeStamp, Matrix4d matrix) {
		matrix = new Matrix4d(matrix);
		
		int n = 0;
		
		boolean flag = false;

		for (int i = 0; i < timeStamps.length; i++) {
			// Replace existing
			if (timeStamp == timeStamps[i]) {
				matrixPositions[i] = matrix;
				flag = true;
				break;
			}
			// Insert at position
			else if (timeStamp > timeStamps[i]) {
				n = i + 1;
			}	
		}
		
		if (!flag) {
			double[] timeStampsNew = new double[timeStamps.length + 1];
			System.arraycopy(timeStamps, 0, timeStampsNew, 0, n);
			timeStampsNew[n] = timeStamp;
			System.arraycopy(timeStamps, n, timeStampsNew, n + 1, timeStamps.length - n);

			Matrix4d[] matrixPositionsNew = new Matrix4d[matrixPositions.length + 1];
			System.arraycopy(matrixPositions, 0, matrixPositionsNew, 0, n);
			matrixPositionsNew[n] = matrix;
			System.arraycopy(matrixPositions, n, matrixPositionsNew, n + 1, timeStamps.length - n);

			timeStamps = timeStampsNew;
			matrixPositions = matrixPositionsNew;
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
	
	public Matrix4d getInvertNormal(double time) {
		ensure(time);
		return cachedMatrixInvertNormal;
	}


	public double[] getArray(double time) {
		ensure(time);
		return (cachedArray);
	}


	public double[] getArrayInvert(double time) {
		ensure(time);
		return (cachedArrayInvert);
	}


	// TODO: Find better name
	private void ensure(double time) {
		if (cachedMatrix == null || time != cachedTime) {
			//System.out.println("Recalculate time at " + time);
			recalculate(time);
		}
	}


	/**
	 * Calculates the lerped Matrix at the requested time. As this AnimatedMatrix
	 * will be referenced many times in a single frame, this is cached so should
	 * only be recalculated once per frame For now this is a straight lerp between
	 * keyframes, we can look into splines/accel later
	 * 
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
			for (int i = 0; i < timeStamps.length - 1; i++) {
				if (time >= timeStamps[i]) {
					idA = i;
					idB = i + 1;
				}
			}
			if (idA == -1) {
				System.out.println("Error: bad time value requested from AnimatedMatrix4d");
			}

			double timeNormalized = (time - timeStamps[idA]) / (timeStamps[idB] - timeStamps[idA]);
			System.out.println(name + " : " + idA + "," + idB + " | " + timeNormalized);

			Vector4d posA = matrixPositions[idA].getColumn(3, new Vector4d());
			Vector4d posB = matrixPositions[idB].getColumn(3, new Vector4d());
			
			Vector3d scaleA = matrixPositions[idA].getScale(new Vector3d());
			Vector3d scaleB = matrixPositions[idB].getScale(new Vector3d());
			Vector3d scaleLerp = scaleA.lerp(scaleB, timeNormalized, new Vector3d());

			// Normalized vs unnormalized?
			Quaterniond quatA = new Quaterniond().setFromUnnormalized(matrixPositions[idA]);
			Quaterniond quatB = new Quaterniond().setFromUnnormalized(matrixPositions[idB]);

			Vector4d posNew = posA.lerp(posB, timeNormalized, new Vector4d());
			Quaterniond quatNew = quatA.slerp(quatB, timeNormalized, new Quaterniond());

			cachedMatrix = new Matrix4d();
			quatNew.get(cachedMatrix);
			cachedMatrix.setColumn(3, posNew);
			
			cachedMatrix.scale(scaleLerp);
		}

		cachedMatrixInvert = new Matrix4d(cachedMatrix).invert();
		cachedMatrixInvertNormal = new Matrix4d(cachedMatrix).normalize3x3().invert();

		cachedMatrix.get(cachedArray);
		cachedMatrixInvert.get(cachedArrayInvert);
		
		cachedTime = time;
	}
}
