package ui;

import geometry.GeometryDatabase;
import graphics.Graphics3D;
import io.Keyboard;
import io.MouseButton;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL33;

import main.Config;
import utility.Color3i;
import utility.Util;
import utility.math.UtilMath;

/**
 * An orthographic or perspective view of a set of geometry.
 *
 */
public class Content_View extends Content {

	private ViewType type;

	// X and Y defines the translation in 2d mode. XYZ define the camera target
	// in 3d mode
	private Vector3d vectorTarget = new Vector3d();
	private Vector3d vectorEye = new Vector3d();

	private Vector3f orthoTarget = new Vector3f();

	// Defines the camera's inclination and azimuth in 3d mode
	private double rotationI = UtilMath.HALF_PI * 4 / 5;
	private double rotationA = UtilMath.HALF_PI / 2;

	/**
	 * Controls zooming in perspective mode
	 */
	public double distance = 150;

	public float scaleFactor = 1f;

	public GeometryDatabase geometry;

	public boolean flipped = false;

	public boolean renderGrid = true;
	public boolean renderAxes = true;

	private float gridSize = 10;

	public float fov;

	public float fovDiff = 0.0f;

	// private EventManager<ViewEvent> viewEventManager;
	
	public double time = 0;


	public Content_View(Panel parent, ViewType type) {
		super(parent);
		this.type = type;
		parent.windowTitle = type.name;
		parent.backgroundColor = Config.getInt("ui.color.background.view", 16);

		vectorTarget = new Vector3d(type.translationX, type.translationY, type.translationZ);
		recalculateEyeVector();
	}


	@SuppressWarnings("static-access")
	@Override
	public void render() {
		int realHeight = getHeight() - parent.barHeight;
		Matrix4f m = new Matrix4f();
		int w = getWidth();
		int h = realHeight;
		float aspect = 1.0f * w / h;

		// Perspective Views
		if (type == ViewType.PERSP) {
			recalculateEyeVector();

			m.setPerspective(fov + fovDiff, aspect, 1.0f, 2550.0f);
			
			Graphics3D.setProjection(m);

			m.setLookAt((float)vectorEye.x, (float)vectorEye.y, (float)vectorEye.z, (float)vectorTarget.x, (float)vectorTarget.y, (float)vectorTarget.z, 0.0f, 0.0f, 1.0f);
			
			Graphics3D.setView(m);
		}
		// Ortho Views
		else {
			m.setOrtho(-w / 2f, w / 2f, -h / 2f, h / 2f, -1, 1);
			Graphics3D.setProjection(m);
			
			m.identity();
			m.translate(orthoTarget.x, orthoTarget.y, orthoTarget.z);
			m.scale(scaleFactor, scaleFactor * (flipped ? -1 : 1),scaleFactor);
			Graphics3D.setView(m);	
		}

		GL33.glEnable(GL33.GL_DEPTH_TEST);
		{
			if (renderGrid) {
				Graphics3D.drawGrid(new Matrix4d().scale(10), new Color3i(178, 178, 178));
			}

			if (renderAxes) {
				Graphics3D.drawAxes(new Matrix4d().scale(100));
			}

			if (geometry != null) {
				geometry.render(time);
			}
		}
		GL33.glDisable(GL33.GL_DEPTH_TEST);

	}


	public void cycle() {
		changeType(type.getNext());
	}


	public void changeType(ViewType newType) {
		changeType(newType, true);
	}


	public void changeType(ViewType newType, boolean moveTarget) {
		this.type = newType;
		setParentWindowTitle(type.name);
	}


	private void rotate(int dx, int dy) {
		if (type != ViewType.PERSP)
			return;

		rotationA += (float) dx / 50;
		rotationI -= (float) dy / 50;
		if (rotationI <= 0) {
			rotationI = 0.00001f;
		}
		if (rotationI > Math.PI) {
			rotationI = Math.PI;
		}
	}


	private void pan(double dx, double dy) {
		// Perspective Views
		if (type == ViewType.PERSP) {

			if (dx != 0) {
				double dA = 0;
				if (dx < 0)
					dA = UtilMath.HALF_PI;
				else if (dx > 0)
					dA = -UtilMath.HALF_PI;
				Vector3d azimuthAngle = Util.sphericalToCartesian(0.3, UtilMath.HALF_PI, rotationA + dA);
				vectorTarget.add(azimuthAngle);
			}
			if (dy != 0) {
				double dI = 0;
				if (dy < 0)
					dI = -UtilMath.HALF_PI;
				else if (dy > 0)
					dI = UtilMath.HALF_PI;
				Vector3d azimuthAngle = Util.sphericalToCartesian(0.3, rotationI + dI, rotationA);
				vectorTarget.add(azimuthAngle);
			}
		}
		// Ortho Views
		else {
			orthoTarget.x += dx;
			orthoTarget.y += dy;
		}
	}


	@Override
	protected void keyPressed(int key) {
		sendMessage(new ViewEvent(ViewEvent.ViewEventType.KEYBOARD));
	}


	@Override
	protected void textInput(char character) {
	}


	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
	}


	@Override
	protected void mouseDragged(int button, int x, int y, int dx, int dy) {
		// This should now be the only location with mouse position flip
		dy = - dy;
		if (MouseButton.instance().rightPressed()) {
			if (type == ViewType.PERSP) {
				if (Keyboard.instance().keyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) {
					pan(dx / 10.0f, dy / 10.0f);
				}
				else {
					rotate(-dx, -dy);
				}
			}
			else {
				pan(dx, dy);
			}
		}
		sendMessage(new ViewEvent(ViewEvent.ViewEventType.MOUSE_DRAGGED));
	}


	@Override
	protected void mouseWheel(int mouseX, int mouseY, int wheelDY) {
		if (type == ViewType.PERSP) {

			distance += -wheelDY * 1;
			if (distance < 1) {
				distance = 1;
			}
		}
		else {
			wheelDY *= 2;
			scaleFactor += wheelDY / 100.0 * scaleFactor;
			if (scaleFactor < 0.01) {
				scaleFactor = 0.01f;
			}

		}
		sendMessage(new ViewEvent(ViewEvent.ViewEventType.MOUSE_WHEEL));
	}

	/*
	 * private PVector screenToGround(int mouseX, int mouseY) { PVector near =
	 * cameraBuffer.unproject(mouseX, mouseY, 0); PVector far =
	 * cameraBuffer.unproject(mouseX, mouseY, 1);
	 * 
	 * far.sub(near); far.normalize(); System.out.println(far); float f =
	 * Math.abs(vectorEye.z / far.z);
	 * 
	 * far.mult(f); far.add(vectorEye); far.z = 0;
	 * 
	 * return (far); }
	 */


	public Vector3d getVectorTarget() {
		return (new Vector3d(vectorTarget));
	}


	public void setVectorTarget(Vector3d v) {
		vectorTarget.x = v.x;
		vectorTarget.y = v.y;
		vectorTarget.z = v.z;
	}


	public Vector3d getVectorEye() {
		recalculateEyeVector();
		return (new Vector3d(vectorEye));
	}


	public void setVectorEye(Vector3d v) {
		vectorEye.set(v);
		Vector3d vectorDiff = new Vector3d(vectorEye).sub(vectorTarget);
		Vector3d sC = Util.cartesianToSpherical(vectorDiff);
		distance = sC.x;
		rotationI = sC.y;
		rotationA = sC.z;
	}


	private void recalculateEyeVector() {
		Vector3d cartesianOffset = Util.sphericalToCartesian(distance, rotationI, rotationA);
		vectorEye = new Vector3d(vectorTarget).add(cartesianOffset);
	}


	public void setOrthoTarget(float x, float y, float z) {
		orthoTarget.set(x, y, z);
	}


	@Override
	public void resizeRespond(int newWidth, int newHeight) {
		// TODO Auto-generated method stub

	}


	@Override
	protected void mouseReleased(int button) {
		// TODO Auto-generated method stub

	}
}
