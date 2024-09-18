package render_sdf.animation;

import org.joml.Vector3d;

public class Vector3dAnimated extends Animated {
	public Vector3d[] vectorPositions;
	
	private Vector3d cachedVector = null;
	
	
	public Vector3dAnimated(double x, double y, double z, String name) {
		timeStamps = new double[1];
		timeStamps[0] = 0;

		vectorPositions = new Vector3d[1];
		vectorPositions[0] = new Vector3d(x,y,z);
		
		this.name = name;
	}
	
	
	public Vector3d get(double time) {
		ensure(time);
		return cachedVector;
	
	}

	
	public void addKeyframe(double timeStamp, Vector3d v) {
		int n = 0;
		
		boolean flag = false;

		for (int i = 0; i < timeStamps.length; i++) {
			// Replace existing
			if (timeStamp == timeStamps[i]) {
				vectorPositions[i] = v;
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

			Vector3d[] vectorPositionsNew = new Vector3d[vectorPositions.length + 1];
			System.arraycopy(vectorPositions, 0, vectorPositionsNew, 0, n);
			vectorPositionsNew[n] = v;
			System.arraycopy(vectorPositions, n, vectorPositionsNew, n + 1, timeStamps.length - n);

			timeStamps = timeStampsNew;
			vectorPositions = vectorPositionsNew;
		}
	}
	
	
	private void ensure(double time) {
		if (cachedVector == null || time != cachedTime) {
			recalculate(time);
		}
	}
	
	
	private void recalculate(double time) {

		// Requested time is before keyframes
		if (time <= timeStamps[0]) {
			cachedVector = vectorPositions[0];
		}
		// Requested time is after keyframes
		else if (time >= timeStamps[timeStamps.length - 1]) {
			cachedVector = vectorPositions[timeStamps.length - 1];
		}
		// Requested time is within keyframes
		else {
			int idA = -1;
			int idB = -1;
			for (int i = 0; i < timeStamps.length; i++) {
				if (time >= timeStamps[i]) {
					idA = i;
					idB = i + 1;
				}
			}
			if (idA == -1) {
				System.out.println("Error: bad time value requested from AnimatedMatrix4d");
			}

			double timeNormalized = (time - timeStamps[idA]) / (timeStamps[idB] - timeStamps[idA]);
			
			System.out.println("Time " + time + " finds ids " + idA + "," + idB + " and factor " + timeNormalized);

			Vector3d vecA = vectorPositions[idA];
			Vector3d vecB = vectorPositions[idB];
			
			cachedVector = vecA.lerp(vecB, timeNormalized,new Vector3d());
		}
		
		cachedTime = time;
	}
}
