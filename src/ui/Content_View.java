package ui;

import geometry.GeometryDatabase;
import io.Keyboard;
import io.MouseButton;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import main.FluxCadd;
import utility.CameraBuffer;
import utility.PVector;
import utility.PVectorD;
import utility.Util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * An orthographic or perspective view of a set of geometry.
 *
 */
public class Content_View extends Content {

	private ViewType type;

	// X and Y defines the translation in 2d mode. XYZ define the camera target
	// in 3d mode
	private PVector vectorTarget = new PVector();
	private PVector vectorEye = new PVector();

	// Defines the camera's inclination and azimuth in 3d mode
	private float rotationI = Util.HALF_PI * 4 / 5;
	private float rotationA = Util.HALF_PI / 2;

	/**
	 * Controls zooming in perspective mode
	 */
	public float distance = 150;

	private float scaleFactor = 1f;

	private CameraBuffer cameraBuffer;

	public GeometryDatabase geometry;

	public boolean flipped = false;

	public boolean renderGrid = true;
	public boolean renderAxes = true;

	private float gridSize = 10;
	
	public float fov;

	// TODO: This is probably deprecated for current intentions
	public boolean tabControl = true;


	public Content_View(Panel parent, ViewType type) {
		super(parent);
		this.type = type;
		parent.windowTitle = type.name;
		parent.backgroundColor = Integer.valueOf(FluxCadd.config.get("ui.color.background.view"), 16);

		vectorTarget = new PVector(type.translationX, type.translationY, type.translationZ);

		cameraBuffer = new CameraBuffer();
	}


	@Override
	public void render() {
		// Some bonkers shit in here;
		// Perspective refused to work with the old GLU I tried to import, so
		// Started using JOML... -> JOML ortho refuses to scale correctly. This for now?
		glColor3f(0, 0, 0);

		glPushMatrix();
		{
			glViewport(getX(), getY(), getWidth(), getHeight());
			FloatBuffer fb = BufferUtils.createFloatBuffer(16);
			Matrix4f m = new Matrix4f();
			int w = getWidth();
			int h = getHeight();
			float aspect = (float) w / h;

			// Perspective Views
			if (type == ViewType.PERSP) {
				PVector cartesianOffset = Util.sphereToCart(distance, rotationI, rotationA);
				vectorEye = vectorTarget.copy();
				vectorEye = PVector.add(vectorTarget, cartesianOffset);

				glMatrixMode(GL_PROJECTION);

				m.setPerspective(fov, aspect, 0.1f, 2550.0f);
				glLoadMatrixf(m.get(fb));

				glMatrixMode(GL_MODELVIEW);
				m.setLookAt(vectorEye.x, vectorEye.y, vectorEye.z, vectorTarget.x, vectorTarget.y, vectorTarget.z, 0.0f, 0.0f, 1.0f);
				glLoadMatrixf(m.get(fb));
			}
			// Ortho Views
			else {
//				glMatrixMode(GL_PROJECTION);
//				//m.setOrtho(-aspect*20.0f,aspect*20.0f,-1*20.0f,1*20.0f,-1,1);
//				m.setOrtho(-w/2f,w/2f,-h/2f,h/2f,-1,1);
//				
//				glLoadMatrixf(m.get(fb));
//					
//				glMatrixMode(GL_MODELVIEW);
//				m.translate(vectorTarget.x,vectorTarget.y,vectorTarget.z);
//				m.scale(scaleFactor,scaleFactor * (flipped ? -1 : 1),scaleFactor);
//				glLoadMatrixf(m.get(fb));

				glMatrixMode(GL_PROJECTION);
				glLoadIdentity();
				glOrtho(-w / 2f, w / 2f, -h / 2f, h / 2f, -1, 1);
				glMatrixMode(GL_MODELVIEW);
				glLoadIdentity();
				glTranslatef(vectorTarget.x, vectorTarget.y, vectorTarget.z);
				glScalef(scaleFactor, scaleFactor * (flipped ? -1 : 1), scaleFactor);
			}

			rendering();

			resetMatrices();
			glOrtho(0, FluxCadd.backend.getWidth(), 0, FluxCadd.backend.getHeight(), -1, 1);
			glViewport(0, 0, FluxCadd.backend.getWidth(), FluxCadd.backend.getHeight());

		}
		glPopMatrix();
	}


	private void resetMatrices() {
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
	}


	private void rendering() {
		glEnable(GL_DEPTH_TEST);

		// Render the grid first, because it should always face the camera
		// in ortho views
		if (renderGrid) {
			renderGrid();
		}

		// Then perform any rotations to correctly show the axes and
		// geometry

		glRotatef(type.rotationX, 1, 0, 0);
		glRotatef(type.rotationY, 0, 1, 0);
		glRotatef(type.rotationZ, 0, 0, 1);

		cameraBuffer.update();

		if (renderAxes) {
			renderAxes();
		}

		glLineWidth(2);
		renderGeometry();

		glDisable(GL_DEPTH_TEST);
	}


	private void renderAxes() {
		float gridTen = gridSize * 10;
		glLineWidth(2);

		glColor3f(1, 0, 0);
		glBegin(GL_LINES);
		glVertex3f(0, 0, 0.01f);
		glVertex3f(gridTen, 0, 0.01f);
		glEnd();

		glColor3f(0, 1, 0);
		glBegin(GL_LINES);
		glVertex3f(0, 0, 0.01f);
		glVertex3f(0, gridTen, 0.01f);
		glEnd();

		glColor3f(0, 0, 1);
		glBegin(GL_LINES);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 0, gridTen);
		glEnd();

		glLineWidth(1);
	}


	private void renderGrid() {
		glColor3f(0.7f, 0.7f, 0.7f);
		glLineWidth(2);
		glBegin(GL_LINES);
		float gridTen = gridSize * 10;
		for (int i = -10; i <= 10; i += 1) {
			glVertex3f(i * gridSize, -gridTen, 0);
			glVertex3f(i * gridSize, gridTen, 0);
			glVertex3f(-gridTen, i * gridSize, 0);
			glVertex3f(gridTen, i * gridSize, 0);
		}
		glEnd();
	}


	private void renderGeometry() {
		if (geometry != null) {
			geometry.render();
		}
	}


	public void cycle() {
		changeType(type.getNext());
	}


	public void changeType(ViewType newType) {
		this.type = newType;
		setParentWindowTitle(type.name);
		vectorTarget = new PVector(type.translationX, type.translationY, type.translationZ);
	}


	private void rotate(int dx, int dy) {
		if (type != ViewType.PERSP)
			return;

		rotationA += (float) dx / 50;
		rotationI -= (float) dy / 50;
		if (rotationI <= 0) {
			rotationI = 0.00001f;
		}
		if (rotationI > Util.PI) {
			rotationI = Util.PI;
		}
			
	}


	private void pan(float dx, float dy) {
		// Perspective Views
		if (type == ViewType.PERSP) {
			if (dx != 0) {
				float dA = 0;
				if (dx < 0)
					dA = Util.HALF_PI;
				else if (dx > 0)
					dA = -Util.HALF_PI;
				PVector azimuthAngle = Util.sphereToCart(0.1f, Util.HALF_PI, rotationA + dA);
				vectorTarget.add(azimuthAngle);
			}
			if (dy != 0) {
				float dI = 0;
				if (dy < 0)
					dI = -Util.HALF_PI;
				else if (dy > 0)
					dI = Util.HALF_PI;
				PVector azimuthAngle = Util.sphereToCart(0.1f, rotationI + dI, rotationA);
				vectorTarget.add(azimuthAngle);
			}
		}
		// Ortho Views
		else {
			vectorTarget.x += dx;
			vectorTarget.y += dy;
		}
	}


	public void setGridSize(float gridSize) {
		this.gridSize = gridSize;
		scaleFactor = 30 / gridSize;
		System.out.println("Grid Size: " + gridSize);
	}


	@Override
	protected void keyPressed(int key) {
		if (key == GLFW_KEY_TAB && tabControl) {
			cycle();
		}

	}


	@Override
	protected void textInput(char character) {
	}


	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
	}


	@Override
	protected void mouseDragged(int dx, int dy) {
		if (MouseButton.instance().rightPressed()) {
			if (type == ViewType.PERSP) {
				if (Keyboard.instance().keyDown(GLFW_KEY_LEFT_SHIFT)) {
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
	}


	@Override
	protected void mouseWheel(float amt) {
		if (type == ViewType.PERSP) {
			distance += -amt * 1;
			if (distance < 1) {
				distance = 1;
			}
		}
		else {
			amt *= 2;
			scaleFactor += amt / 100 * scaleFactor;
			if (scaleFactor < 0.01)
				scaleFactor = 0.01f;
		}

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
	
	public PVector getVectorTarget() {
		return(vectorTarget.copy());
	}
	
	public PVector getVectorEye() {
		return(vectorEye.copy());
	}


	public void setVectorTarget(PVector v) {
		this.vectorTarget = v.copy();
	}


	public void setVectorTarget(PVectorD v) {
		vectorTarget.x = (float) v.x;
		vectorTarget.y = (float) v.y;
		vectorTarget.z = (float) v.z;
	}


	public void setVectorEye(PVector v) {
		this.vectorEye = v.copy();
	}


	public void setVectorEye(PVectorD v) {
		vectorEye.x = (float) v.x;
		vectorEye.y = (float) v.y;
		vectorEye.z = (float) v.z;

	}


	public void setScaleFactor(float f) {
		this.scaleFactor = f;
		System.out.println("Scale Factor : " + f);
	}
}
