package render_sdf.renderer;

import javax.vecmath.*;

import utility.PVector;
import utility.PVectorD;
import utility.Util;

public class Camera {
	private PVectorD position = new PVectorD(0,0,0);
	private PVectorD target = new PVectorD(0,100,0);
	public double fov = Math.toRadians(45);

	private int displayWidth;
	private int displayHeight;

	private Matrix4d extrinsic = null;
	private float focalLength;


	public Camera(int displayWidth, int displayHeight) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;

		this.focalLength = displayWidth * 0.84f;

		this.extrinsic = new Matrix4d(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);

		updateMatrix();
	}


	public void updateMatrix() {
		PVectorD vecDiff = PVectorD.sub(target, position);
		PVectorD sphere = Util.cartesianToSpherical(vecDiff);
		
		sphere.y = (Math.PI / 2) - sphere.y;
		sphere.z -= (Math.PI / 2);
		
		extrinsic.setIdentity();

		Matrix4d inclinationRotation = new Matrix4d();
		inclinationRotation.rotX(sphere.y);
		Matrix4d azimuthRotation = new Matrix4d();
		azimuthRotation.rotZ(sphere.z);

		
		//TODO: Is this backwards / should inclination be with rotY?
		extrinsic.mul(azimuthRotation);
		extrinsic.mul(inclinationRotation);
	}


	/**
	 * Original version for how rays were generated, realized was incorrect (but
	 * interesting?)
	 * 
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


	// TODO : Temporary until Vector methods get replaced
	public PVectorD getRayVector(int x, int y) {
		//Vector3f out = new Vector3f(-focalLength, -(x - (displayWidth / 2)), -(y - displayHeight / 2));
		Vector3f out = new Vector3f((x - (displayWidth / 2)), focalLength,-(y - displayHeight / 2));
		out.normalize();

		extrinsic.transform(out);

		return (new PVectorD(out.x, out.y, out.z));
	}
	
	
	public PVectorD getPosition() {
		return(position.copy());
	}
	
	
	public PVectorD getTarget() {
		return(target.copy());
	}
	
	public void setPosition(PVector v) {
		this.position.x = v.x;
		this.position.y = v.y;
		this.position.z = v.z;
		updateMatrix();
	}
	
	public void setPosition(PVectorD position) {
		this.position = position.copy();
		updateMatrix();
	}
	
	public void setTarget(PVector v) {
		this.target.x = v.x;
		this.target.y = v.y;
		this.target.z = v.z;
		updateMatrix();
	}
	
	
	public void setTarget(PVectorD target) {
		this.target = target.copy();
		updateMatrix();
	}
}
