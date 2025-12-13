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
 * The camera object for SDF rendering operations, defined by an eye position and target position
 */
public class Camera {
	/**
	 * The 'eye' position of the camera
	 */
	public Vector3dAnimated position = new Vector3dAnimated(0, 0, 0, "Camera");
	
	/**
	 * The the camera faces along the vector from position to target. The length of this vector does not currently matter. 
	 * We currently assume the camera facespositive Z.
	 */
	private Vector3dAnimated target = new Vector3dAnimated(0, 100, 0, "Camera");
	
	/**
	 * Field-of-view angle in radians. 
	 */
	private double fov = Math.toRadians(70);
	
	private double focalLength;

	/**
	 * The output image width in pixels
	 */
	private int displayWidth;
	
	/**
	 * The output image height in pixels
	 */
	private int displayHeight;

	/**
	 * Stores the (rotation) matrix for transforming sensor-pixel locations to ray direction vectors
	 */
	private Matrix4dAnimated extrinsic = null;
	

	/**
	 * Stores the 'box and pyramid' geometry when viewing the camera in third person
	 */
	private Group internalGeometryThirdPerson;
	
	
	/**
	 * Stores the framing geometry when looking directly through the camera
	 */
	private Group internalGeometryFirstPerson;


	public Camera(int displayWidth, int displayHeight) {
		this.displayWidth = displayWidth;
		this.displayHeight = displayHeight;

		this.focalLength = displayHeight / Math.tan(fov);

		this.extrinsic = new Matrix4dAnimated(new Matrix4d(), "Camera Extrinsic");

		generateGeometry();
	}

	
	public Vector3d getRayVector(int x, int y, double timestamp) {
		Vector3d out = new Vector3d((x - (displayWidth / 2)), focalLength, -(y - displayHeight / 2));
		out.normalize();
		out.mulDirection(extrinsic.get(timestamp));
	
		return out;
	}


	/**
	 * Get a copy of the camera position at a given time
	 * @param time
	 * @return
	 */
	public Vector3d getPosition(double time) {
		return (new Vector3d(position.get(time)));
	}

	
	/**
	 * Get a copy of the camera target vector at a given time
	 * @param time
	 * @return
	 */
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
		return internalGeometryFirstPerson;

	}


	public Group getGeometryThirdPerson() {
		return internalGeometryThirdPerson;
	}
	
	public double getFOV() {
		return(fov);
	}
	
	public void setFOV(double fov) {
		this.fov = fov;
		this.focalLength = displayHeight / Math.tan(fov);
	}


	
	/**
	 * Changes the shape of the camera geometry
	 * Only gets called when changing fov
	 */
	public void updateGeometry() {
		Rect rect = ((Rect)internalGeometryFirstPerson.getChild(0));
		Matrix4d matrix = rect.modelMatrix.get(0);
		double newD = 0.5;
		double trueD = displayHeight / Math.tan(fov);
		double dScale = newD / trueD;
		double borderWidth = displayWidth * dScale;
		double borderHeight = displayHeight * dScale;
		
		matrix.m00(borderWidth / 2);
		matrix.m12(borderHeight / 2);
		
		rect.modelMatrix.addKeyframe(0, matrix);
		
		rect.setupVAO();
	}
	
	
	public int getPixelWidth() {
		return displayWidth;
	}
	
	
	public int getPixelHeight() {
		return displayHeight;
	}
	
	
	/**
	 * Call after performing a block of position or target keyframe changes
	 */
	public void updateMatrices() {
		for (double d : position.getKeyframes()) {
			updateExtrinsicMatrix(d);
		}
		
		for (double d : target.getKeyframes()) {
			updateExtrinsicMatrix(d);
		}
	}
	
	
	/**
	 * Adds an extrinsic matrix keyframe to match the state of the position and target
	 * @param time
	 */
	private void updateExtrinsicMatrix(double time) {
		Vector3d vecDiff = new Vector3d(target.get(time)).sub(position.get(time));
		Vector3d sphere = Util.cartesianToSpherical(vecDiff);

		sphere.y = (Math.PI / 2) - sphere.y;
		sphere.z -= (Math.PI / 2);
		
		Matrix4d m = new Matrix4d();

		m.rotate(sphere.z, 0, 0, 1);
		m.rotate(sphere.y, 1, 0, 0);

		m.setColumn(3, new Vector4d(position.get(time), 1));
		
		extrinsic.addKeyframe(time, m);
	}
	
	
	/**
	 * Creates the geometry for the third and first person representations
	 */
	private void generateGeometry() {
		internalGeometryThirdPerson = new Group();
		// TODO: Clean up how scale is set here
		internalGeometryThirdPerson.add(new Box(new Matrix4d().m00(3).m11(3).m22(3)).clearFillColor());
		Line igLens = new Line(new Vector3d(0, 0, 0), new Vector3d(0, 20, 0));
		internalGeometryThirdPerson.add(igLens);
		internalGeometryThirdPerson.setMatrix(extrinsic);

		double newD = 0.5;
		double trueD = displayHeight / Math.tan(fov);
		double dScale = newD / trueD;
		double borderWidth = displayWidth * dScale;
		double borderHeight = displayHeight * dScale;
		internalGeometryFirstPerson = new Group();
		internalGeometryFirstPerson.add(new Rect(0, newD, 0, borderWidth, borderHeight, 0, Math.PI / 2));
		internalGeometryFirstPerson.add(new Line(new Vector3d(0, 0, 0), new Vector3d(0, newD, 0)));
		internalGeometryFirstPerson.setMatrix(extrinsic);
	}
}
