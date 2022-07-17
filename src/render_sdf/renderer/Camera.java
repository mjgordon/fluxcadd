package render_sdf.renderer;

import javax.vecmath.*;

import utility.PVectorD;
import utility.Util;

public class Camera {
	public PVectorD position = new PVectorD(100, 0, 10);
	public PVectorD target = new PVectorD(0, 0, 10);
	public double fov = Math.toRadians(45);

	public int displayWidth;
	public int displayHeight;

	public Matrix4f extrinsic = null;
	float focalLength;

	public Camera(int displayWidth, int displayHeight) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;
		
		this.focalLength = displayWidth * 0.84f;
		
		this.extrinsic = new Matrix4f(1, 0, 0, 100, 0, 1, 0, 0, 0, 0, 1, 10, 0, 0, 0, 1);
	}

	
	/**
	 * Original version for how rays were generated, realized was incorrect (but interesting?)
	 * @param x
	 * @param y
	 * @return
	 */
	public PVectorD getRayVectorSpherical(int x, int y) {
		double cameraAngle = Math.atan2(target.y - position.y, target.x - position.x);
		double azimuth = Util.map(x, 0, displayWidth, -fov / 2, fov / 2);
		double inclination = Util.map(y, 0, displayHeight, (Math.PI / 2) - (fov / 2), (Math.PI / 2) + (fov / 2));

		PVectorD rayVector = Util.sphericalToCartesian(1, azimuth + cameraAngle, inclination);
		return (rayVector);
	}
	
	public PVectorD getRayVector(int x, int y) {
		PVectorD out = new PVectorD(-focalLength,-(x - (displayWidth /2)),-(y-displayHeight / 2));
		out.normalize();
		return(out);
	}
}
