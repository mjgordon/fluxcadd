package render_sdf.renderer;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import utility.Util;

public class Camera {
	private Vector3d position = new Vector3d(0, 0, 0);
	private Vector3d target = new Vector3d(0, 100, 0);
	public double fov = Math.toRadians(45);

	private int displayWidth;
	private int displayHeight;

	private Matrix4d extrinsic = null;
	private double focalLength;


	public Camera(int displayWidth, int displayHeight) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;

		this.focalLength = displayWidth * 0.84f;

		this.extrinsic = new Matrix4d();

		updateMatrix();
	}


	public void updateMatrix() {
		Vector3d vecDiff = new Vector3d(target).sub(position);
		Vector3d sphere = Util.cartesianToSpherical(vecDiff);

		sphere.y = (Math.PI / 2) - sphere.y;
		sphere.z -= (Math.PI / 2);

		extrinsic.identity();

		extrinsic.rotate(sphere.z, 0, 0, 1);
		extrinsic.rotate(sphere.y, 1, 0, 0);

		/*
		 * Matrix4d inclinationRotation = new Matrix4d();
		 * inclinationRotation.rotX(sphere.y); Matrix4d azimuthRotation = new
		 * Matrix4d(); azimuthRotation.rotZ(sphere.z);
		 * 
		 * 
		 * //TODO: Is this backwards / should inclination be with rotY?
		 * extrinsic.mul(azimuthRotation); extrinsic.mul(inclinationRotation);
		 */
	}


	/**
	 * Original version for how rays were generated, realized was incorrect (but
	 * interesting?)
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Vector3d getRayVectorSpherical(int x, int y) {
		double cameraAngle = Math.atan2(target.y - position.y, target.x - position.x);
		double azimuth = Util.map(x, 0, displayWidth, -fov / 2, fov / 2);
		double inclination = Util.map(y, 0, displayHeight, (Math.PI / 2) - (fov / 2), (Math.PI / 2) + (fov / 2));

		Vector3d rayVector = Util.sphericalToCartesian(1, azimuth + cameraAngle, inclination);
		return (rayVector);
	}


	public Vector3d getRayVector(int x, int y) {
		Vector3d out = new Vector3d((x - (displayWidth / 2)), focalLength, -(y - displayHeight / 2));
		out.normalize();
		extrinsic.transformPosition(out);

		return (out);
	}


	public Vector3d getPosition() {
		return (new Vector3d(position));
	}


	public Vector3d getTarget() {
		return (new Vector3d(target));
	}


	public void setPosition(Vector3d v) {
		this.position.x = v.x;
		this.position.y = v.y;
		this.position.z = v.z;
		updateMatrix();
	}


	public void setTarget(Vector3d v) {
		this.target.x = v.x;
		this.target.y = v.y;
		this.target.z = v.z;
		updateMatrix();
	}
}
