package render_sdf.renderer;

import javax.vecmath.*;

import utility.VectorD;
import utility.Util;

@SuppressWarnings("static-access")
public class Camera {
	public VectorD position = new VectorD(100, 0, 10);
	public VectorD target = new VectorD(0, 0, 10);
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

	public VectorD getRayVectorSpherical(int x, int y) {
		double cameraAngle = Math.atan2(target.y - position.y, target.x - position.x);
		double azimuth = Util.map(x, 0, displayWidth, -fov / 2, fov / 2);
		double inclination = Util.map(y, 0, displayHeight, (Math.PI / 2) - (fov / 2), (Math.PI / 2) + (fov / 2));

		VectorD rayVector = Util.sphericalToCartesian(1, azimuth + cameraAngle, inclination);
		System.out.println(rayVector);
		return (rayVector);
	}
	
	public VectorD getRayVector(int x, int y) {
		//Vector4f point = new Vector4f(x - (displayWidth / 2),focalLength,y - (displayHeight / 2),0);
		
		//transform = point.dot(extrinsic)
		
		VectorD out = new VectorD(-focalLength,-(x - (displayWidth /2)),-(y-displayHeight / 2));
		out.normalize();
		//System.out.println(out);
		return(out);
	}
}
