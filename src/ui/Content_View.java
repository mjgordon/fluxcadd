package ui;

import geometry.GeometryDatabase;
import graphics.OGLWrapper;
import io.Keyboard;
import io.MouseButton;

import java.nio.DoubleBuffer;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import main.Config;
import main.FluxCadd;
import utility.CameraBuffer;
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

	private Vector3d orthoTarget = new Vector3d();

	// Defines the camera's inclination and azimuth in 3d mode
	private double rotationI = UtilMath.HALF_PI * 4 / 5;
	private double rotationA = UtilMath.HALF_PI / 2;

	/**
	 * Controls zooming in perspective mode
	 */
	public double distance = 150;

	private double scaleFactor = 1f;

	private CameraBuffer cameraBuffer;

	public GeometryDatabase geometry;

	public boolean flipped = false;

	public boolean renderGrid = true;
	public boolean renderAxes = true;

	private float gridSize = 10;

	public double fov;

	public double fovDiff = 0.0;

	// private EventManager<ViewEvent> viewEventManager;
	
	public double time = 0;


	public Content_View(Panel parent, ViewType type) {
		super(parent);
		this.type = type;
		parent.windowTitle = type.name;
		parent.backgroundColor = Config.getInt("ui.color.background.view", 16);

		vectorTarget = new Vector3d(type.translationX, type.translationY, type.translationZ);
		recalculateEyeVector();

		cameraBuffer = new CameraBuffer();

		// viewEventManager = new EventManager<ViewEvent>();
	}


	@Override
	public void render() {
		GL11.glColor3f(0, 0, 0);

		GL11.glPushMatrix();
		{
			int realHeight = getHeight() - parent.barHeight;
			GL11.glViewport(getX(), FluxCadd.getHeight() - getHeight() - getY(), getWidth(), realHeight);
			DoubleBuffer db = BufferUtils.createDoubleBuffer(16);
			Matrix4d m = new Matrix4d();
			int w = getWidth();
			int h = realHeight;
			double aspect = 1.0 * w / h;

			// Perspective Views
			if (type == ViewType.PERSP) {
				recalculateEyeVector();

				GL11.glMatrixMode(GL11.GL_PROJECTION);

				m.setPerspective(fov + fovDiff, aspect, 0.1, 2550.0);
				GL11.glLoadMatrixd(m.get(db));

				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				m.setLookAt(vectorEye.x, vectorEye.y, vectorEye.z, vectorTarget.x, vectorTarget.y, vectorTarget.z, 0.0, 0.0, 1.0);
				GL11.glLoadMatrixd(m.get(db));
			}
			// Ortho Views
			else {
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glOrtho(-w / 2f, w / 2f, -h / 2f, h / 2f, -1, 1);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				GL11.glTranslated(orthoTarget.x, orthoTarget.y, orthoTarget.z);
				// TODO: here is a possible location of extra flipping
				GL11.glScaled(scaleFactor, scaleFactor * (flipped ? -1 : 1), scaleFactor);
			}

			rendering();

			resetMatrices();
			GL11.glOrtho(0, FluxCadd.getWidth(), 0, FluxCadd.getHeight(), -1, 1);
			GL11.glViewport(0, 0, FluxCadd.getWidth(), FluxCadd.getHeight());
		}
		GL11.glPopMatrix();
	}


	private void resetMatrices() {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
	}


	private void rendering() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);

		// Render the grid first, because it should always face the camera
		// in ortho views
		if (renderGrid) {
			renderGrid();
		}

		// Then perform any rotations to correctly show the axes and
		// geometry

		GL11.glRotatef(type.rotationX, 1, 0, 0);
		GL11.glRotatef(type.rotationY, 0, 1, 0);
		GL11.glRotatef(type.rotationZ, 0, 0, 1);

		cameraBuffer.update();

		if (renderAxes) {
			renderAxes();
		}

		OGLWrapper.glLineWidth(2);
		renderGeometry();

		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}


	private void renderAxes() {
		float gridTen = gridSize * 10;
		OGLWrapper.glLineWidth(2);

		GL11.glColor3f(1, 0, 0);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(0, 0, 0.01f);
		GL11.glVertex3f(gridTen, 0, 0.01f);
		GL11.glEnd();

		GL11.glColor3f(0, 1, 0);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(0, 0, 0.01f);
		GL11.glVertex3f(0, gridTen, 0.01f);
		GL11.glEnd();

		GL11.glColor3f(0, 0, 1);
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(0, 0, 0);
		GL11.glVertex3f(0, 0, gridTen);
		GL11.glEnd();

		OGLWrapper.glLineWidth(1);
	}


	private void renderGrid() {
		GL11.glColor3d(0.7, 0.7, 0.7);
		OGLWrapper.glLineWidth(2);
		GL11.glBegin(GL11.GL_LINES);
		float gridTen = gridSize * 10;
		for (int i = -10; i <= 10; i += 1) {
			GL11.glVertex3d(i * gridSize, -gridTen, 0);
			GL11.glVertex3d(i * gridSize, gridTen, 0);
			GL11.glVertex3d(-gridTen, i * gridSize, 0);
			GL11.glVertex3d(gridTen, i * gridSize, 0);
		}
		GL11.glEnd();
	}


	private void renderGeometry() {
		if (geometry != null) {
			geometry.render(time);
		}
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


	public void setGridSize(float gridSize) {
		this.gridSize = gridSize;
		scaleFactor = 30 / gridSize;
		System.out.println("Grid Size: " + gridSize);
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
	protected void mouseDragged(int button, int dx, int dy) {
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
	protected void mouseWheel(float amt) {
		if (type == ViewType.PERSP) {

			distance += -amt * 1;
			if (distance < 1) {
				distance = 1;
			}

			// fovDiff += (amt * 0.1);
		}
		else {
			amt *= 2;
			scaleFactor += amt / 100 * scaleFactor;
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
		vectorEye = new Vector3d(v);
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


	public void setOrthoTarget(Vector3d v) {
		orthoTarget.x = v.x;
		orthoTarget.y = v.y;
		orthoTarget.z = v.z;
	}


	public void setScaleFactor(double d) {
		this.scaleFactor = d;
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
