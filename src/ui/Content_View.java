package ui;

import io.Keyboard;
import io.MouseButton;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import lisp.GeometryFile;
import main.FluxCadd;
import utility.CameraBuffer;
import utility.PVector;
import utility.Util;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class Content_View extends Content {

	private ViewType type;

	// X and Y defines the translation in 2d mode. XYZ define the camera target
	// in 3d mode
	private PVector vectorTarget = new PVector();
	private PVector vectorEye = new PVector();

	// Defines the camera's inclination and azimuth in 3d mode
	private float rotationI = Util.HALF_PI * 4 / 5;
	private float rotationA = Util.HALF_PI / 2;

	private float distance = 150;

	private float scaleFactor = 1f;

	private CameraBuffer cameraBuffer;

	public GeometryFile geometry;

	public boolean flipped = false;

	public Content_View(Panel parent, ViewType type) {
		super(parent);
		this.type = type;
		parent.windowTitle = type.name;

		vectorTarget = new PVector(type.translationX, type.translationY, type.translationZ);

		cameraBuffer = new CameraBuffer();
	}

	@Override
	public void render() {
		//Some bonkers shit in here;
		//Perspective refused to work with the old GLU I tried to import, so 
		//Started using JOML... -> JOML ortho refuses to scale correctly. This for now?
		glColor3f(0, 0, 0);

		glPushMatrix();
		{
			glViewport(getX(), getY(), getWidth(), getHeight());
			FloatBuffer fb = BufferUtils.createFloatBuffer(16);
			Matrix4f m = new Matrix4f();
			int w = getWidth();
			int h = getHeight();
			float aspect = (float) w / h;
			
			//Perspective Views
			if (type == ViewType.PERSP) {
				PVector cartesianOffset = Util.sphereToCart(distance, rotationI, rotationA);
				vectorEye = vectorTarget.get();
				vectorEye = PVector.add(vectorTarget, cartesianOffset);
				
				glMatrixMode(GL_PROJECTION);
				
				m.setPerspective((float) Math.toRadians(45.0f), aspect, 0.1f, 2550.0f);
				glLoadMatrixf(m.get(fb));
				
				
				glMatrixMode(GL_MODELVIEW);
				m.setLookAt(vectorEye.x, vectorEye.y, vectorEye.z,
							vectorTarget.x, vectorTarget.y, vectorTarget.z,
							0.0f, 0.0f, 1.0f);
				glLoadMatrixf(m.get(fb));
			} 
			//Ortho Views
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
				glOrtho(-w/2f,w/2f,-h/2f,h/2f,-1,1);
				glMatrixMode(GL_MODELVIEW);
				glLoadIdentity();
				glTranslatef(vectorTarget.x, vectorTarget.y, vectorTarget.z);
				glScalef(scaleFactor,scaleFactor * (flipped ? -1 : 1),scaleFactor);
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
		renderGrid();

		// Then perform any rotations to correctly show the axes and
		// geometry
		
		glRotatef(type.rotationX, 1, 0, 0);
		glRotatef(type.rotationY, 0, 1, 0);
		glRotatef(type.rotationZ, 0, 0, 1);

		cameraBuffer.update();

		renderAxes();
		renderGeometry();

		glDisable(GL_DEPTH_TEST);
	}
	

	private void renderAxes() {
		glLineWidth(2);
		
		glColor3f(1, 0, 0);
		glBegin(GL_LINES);
		glVertex3f(0, 0, 0.1f);
		glVertex3f(100, 0, 0.1f);
		glEnd();

		glColor3f(0, 1, 0);
		glBegin(GL_LINES);
		glVertex3f(0, 0, 0.1f);
		glVertex3f(0, 100, 0.1f);
		glEnd();

		glColor3f(0, 0, 1);
		glBegin(GL_LINES);
		glVertex3f(0, 0, 0);
		glVertex3f(0, 0, 100);
		glEnd();
		
		glLineWidth(1);
	}

	private void renderGrid() {
		glColor3f(0.7f, 0.7f, 0.7f);
		glBegin(GL_LINES);
		for (int i = -100; i <= 100; i += 10) {
			glVertex3f(i, -100, 0);
			glVertex3f(i, 100, 0);
			glVertex3f(-100, i, 0);
			glVertex3f(100, i, 0);
		}
		glEnd();
	}

	private void renderGeometry() {
		if (geometry != null)
			geometry.render();
	}

	public void cycle() {
		changeType(type.getNext());
	}

	public void changeType(ViewType newType) {
		this.type = newType;
		setParentWindowTitle(type.name);
		vectorTarget = new PVector(type.translationX, type.translationY, type.translationZ);
	}

	public void rotate(int dx, int dy) {
		if (type != ViewType.PERSP)
			return;

		rotationA += (float) dx / 50;
		rotationI -= (float) dy / 50;
		if (rotationI <= 0)
			rotationI = 0.00001f;
		if (rotationI > Util.PI)
			rotationI = Util.PI;
	}

	public void pan(int dx, int dy) {
		if (type == ViewType.PERSP) {
			if (dx != 0) {
				float dA = 0;
				if (dx < 0)
					dA = Util.HALF_PI;
				else if (dx > 0)
					dA = -Util.HALF_PI;
				PVector azimuthAngle = Util.sphereToCart(1, Util.HALF_PI, rotationA + dA);
				vectorTarget.add(azimuthAngle);
			}
			if (dy != 0) {
				float dI = 0;
				if (dy < 0)
					dI = -Util.HALF_PI;
				else if (dy > 0)
					dI = Util.HALF_PI;
				PVector azimuthAngle = Util.sphereToCart(1, rotationI + dI, rotationA);
				vectorTarget.add(azimuthAngle);
			}
		} else {
			vectorTarget.x += dx;
			vectorTarget.y += dy;
		}

	}

	@Override
	protected void keyPressed(int key) {
		if (key == GLFW_KEY_TAB)
			cycle();
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
				if (Keyboard.instance().keyDown(GLFW_KEY_LEFT_SHIFT)) 
					pan(dx, dy);
				else
					rotate(-dx, -dy);
			} else
				pan(dx, dy);
		}
	}

	@Override
	protected void mouseWheel(float amt) {
		if (type == ViewType.PERSP) {
			distance += -amt * 5;
		} else {
			amt *= 2;
			scaleFactor += amt / 100 * scaleFactor;
			if (scaleFactor < 0.01)
				scaleFactor = 0.01f;
		}

	}

	public PVector screenToGround(int mouseX, int mouseY) {
		PVector near = cameraBuffer.unproject(mouseX, mouseY, 0);
		PVector far = cameraBuffer.unproject(mouseX, mouseY, 1);

		far.sub(near);
		far.normalize();
		System.out.println(far);
		float f = Math.abs(vectorEye.z / far.z);

		far.mult(f);
		far.add(vectorEye);
		far.z = 0;

		return (far);
	}

}
