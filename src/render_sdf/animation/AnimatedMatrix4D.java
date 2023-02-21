package render_sdf.animation;

import org.joml.Matrix4d;

public class AnimatedMatrix4D {
	private double[] timeStamps;
	private Matrix4d[] matrixPositions;
	
	private double cachedTime = Double.NaN;
	
	private Matrix4d cachedMatrix = null;
	private Matrix4d cachedMatrixInvert = null;
	
	public AnimatedMatrix4D(Matrix4d base) {
		timeStamps = new double[1];
		timeStamps[0] = 0;
		
		matrixPositions = new Matrix4d[1];
		matrixPositions[0] = base;
	}
	
	public Matrix4d get(double time) {
		if (cachedMatrix == null || time != cachedTime) {
			recalculate(time);
		}
		return cachedMatrix;
	}
	
	public Matrix4d getInvert(double time) {
		if (cachedMatrixInvert == null || time != cachedTime) {
			recalculate(time);
		}
		return cachedMatrixInvert;
	}
	
	private void recalculate(double time) {
		
	}
}
