package render_sdf.renderer;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.Box;
import geometry.Group;
import geometry.Line;
import geometry.Rect;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.animation.Vector3dAnimated;
import utility.Util;

/**
 * A camera object defined by an eye position and target position
 * TODO: The usage of updateGeometry and updateMatrix needs to be cleaned up, and additionally shouldn't be recreating the Matrix4dAnimated each time
 */
public class Camera {
	public Vector3dAnimated position = new Vector3dAnimated(0, 0, 0, "Camera");
	private Vector3dAnimated target = new Vector3dAnimated(0, 100, 0, "Camera");
	private double fov = Math.toRadians(70);

	/**
	 * The output image width in pixels
	 */
	private int displayWidth;
	
	/**
	 * The output image height in pixels
	 */
	private int displayHeight;

	private Matrix4d extrinsic = null;
	private double focalLength;

	private Group internalGeometryThirdPerson;
	private Group internalGeometryFirstPerson;


	public Camera(int displayWidth, int displayHeight) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;

		this.focalLength = displayHeight / Math.tan(fov);

		this.extrinsic = new Matrix4d();

		generateGeometry();

		updateMatrix(0);
	}


	public void updateMatrix(double time) {
		Vector3d vecDiff = new Vector3d(target.get(time)).sub(position.get(time));
		Vector3d sphere = Util.cartesianToSpherical(vecDiff);

		sphere.y = (Math.PI / 2) - sphere.y;
		sphere.z -= (Math.PI / 2);

		extrinsic.identity();

		extrinsic.rotate(sphere.z, 0, 0, 1);
		extrinsic.rotate(sphere.y, 1, 0, 0);

		Matrix4d matrixGeometry = new Matrix4d(extrinsic).setColumn(3, new Vector4d(position.get(time), 1));
		
		internalGeometryThirdPerson.setMatrix(new Matrix4dAnimated(matrixGeometry, "CameraThirdPerson"));
		internalGeometryFirstPerson.setMatrix(new Matrix4dAnimated(matrixGeometry, "CameraFirstPerson"));
	}


	public Vector3d getRayVector(int x, int y) {
		Vector3d out = new Vector3d((x - (displayWidth / 2)), focalLength, -(y - displayHeight / 2));
		out.normalize();
		extrinsic.transformPosition(out);

		return (out);
	}


	public Vector3d getPosition(double time) {
		return (new Vector3d(position.get(time)));
	}


	public Vector3d getTarget(double time) {
		return (new Vector3d(target.get(time)));
	}


	public void setPositionKeyframe(double time, Vector3d v) {
		position.addKeyframe(time, v);
	}


	public void setTargetKeyframe(double time, Vector3d v) {
		target.addKeyframe(time, v);
	}


	public Group getGeometryFirstPerson() {
		return (internalGeometryFirstPerson);

	}


	public Group getGeometryThirdPerson() {
		return (internalGeometryThirdPerson);
	}
	
	public double getFOV() {
		return(fov);
	}
	
	public void setFOV(double fov) {
		this.fov = fov;
		this.focalLength = displayHeight / Math.tan(fov);
	}


	
	/**
	 * Only gets called when changing fov
	 */
	public void updateGeometry(double time) {
		Rect rect = ((Rect)internalGeometryFirstPerson.getChild(0));
		Matrix4d matrix = rect.matrix.get(time);
		double newD = 0.5;
		double trueD = displayHeight / Math.tan(fov);
		double dScale = newD / trueD;
		double borderWidth = displayWidth * dScale;
		double borderHeight = displayHeight * dScale;
		
		matrix.m00(borderWidth / 2);
		matrix.m12(borderHeight / 2);
		
		rect.setMatrix(new Matrix4dAnimated(matrix, "CameraFirstPersonRect"));
		
		rect.recalculateExplicitGeometry();
	}
	
	
	/**
	 * Creates the geometry for the third and first person representations
	 */
	private void generateGeometry() {
		internalGeometryThirdPerson = new Group();
		internalGeometryThirdPerson.add(new Box(new Matrix4d().m00(3).m11(3).m22(3)).clearFillColor());
		Line igLens = new Line(new Vector3d(0, 0, 0), new Vector3d(0, 20, 0));
		internalGeometryThirdPerson.add(igLens);

		double newD = 0.5;
		double trueD = displayHeight / Math.tan(fov);
		double dScale = newD / trueD;
		double borderWidth = displayWidth * dScale;
		double borderHeight = displayHeight * dScale;
		internalGeometryFirstPerson = new Group();
		internalGeometryFirstPerson.add(new Rect(0, newD, 0, borderWidth, borderHeight, 0, Math.PI / 2));
		internalGeometryFirstPerson.add(new Line(new Vector3d(0, 0, 0), new Vector3d(0, newD, 0)));
		internalGeometryFirstPerson.setMatrix(new Matrix4dAnimated(extrinsic, "CameraFirstPerson"));
	}
}
