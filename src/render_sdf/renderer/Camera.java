package render_sdf.renderer;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.Box;
import geometry.Group;
import geometry.Line;
import utility.Util;
import utility.math.UtilMath;

public class Camera {
	private Vector3d position = new Vector3d(0, 0, 0);
	private Vector3d target = new Vector3d(0, 100, 0);
	public double fov = Math.toRadians(45);

	private int displayWidth;
	private int displayHeight;

	private Matrix4d extrinsic = null;
	private double focalLength;
	
	private Group internalGeometry;


	public Camera(int displayWidth, int displayHeight) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;

		this.focalLength = displayWidth * 0.84f;

		this.extrinsic = new Matrix4d();
		
		internalGeometry = new Group();
		internalGeometry.add(new Box(new Matrix4d().m00(3).m11(3).m22(3)).clearFillColor());
		Line igLens = new Line(new Vector3d(0,0,0),new Vector3d(0,20,0));
		internalGeometry.add(igLens);
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
		
		internalGeometry.setFrame(new Matrix4d(extrinsic).setColumn(3, new Vector4d(position,1)));
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
		double azimuth = UtilMath.map(x, 0, displayWidth, -fov / 2, fov / 2);
		double inclination = UtilMath.map(y, 0, displayHeight, (Math.PI / 2) - (fov / 2), (Math.PI / 2) + (fov / 2));

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
	
	public Group getGeometry() {
		return(internalGeometry);
	}
}
