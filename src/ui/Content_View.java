package ui;

import lisp.GeometryFile;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import org.lwjgl.util.glu.GLU;


import utility.CameraBuffer;
import utility.PVector;
import utility.Util;

import static org.lwjgl.opengl.GL11.*;
public class Content_View extends Content {
	
	private ViewType type;
	
	//X and Y defines the translation in 2d mode. XYZ define the camera target in 3d mode
	public PVector translation;
	public PVector origin;
	
	//Defines the camera's inclination and azimuth in 3d mode
	public float rotationI = Util.HALF_PI * 4 / 5;
	public float rotationA = Util.HALF_PI / 2;
	
	public float distance = 100;
	
	public float scaleFactor = 1.0f;
	
	public CameraBuffer cameraBuffer;
	
	public GeometryFile geometry;
	
	public boolean flipped = false;
	
	public Content_View(Window parent, ViewType type) {
		this.type = type;
		this.parent = parent;
		parent.windowTitle = type.name;
		
		translation = new PVector(type.translationX,type.translationY,type.translationZ);
		
		cameraBuffer = new CameraBuffer();
	}

	public void render() {
		glColor3f(0,0,0);
		glPushMatrix();
		glViewport(getX(),getY(),parent.getWidth(),parent.getHeight());
		
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glPushMatrix();
		
		if (type == ViewType.PERSP) {
			//float aspect = (float)Display.getWidth()/Display.getHeight();
			float aspect = (float)parent.getWidth() / parent.getHeight();
			GLU.gluPerspective(45, aspect, 0.01f,255);
		}
		else glOrtho(0,parent.getWidth(),0,parent.getHeight(), -100,100);
		
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glPushMatrix();
		
		origin = translation.get();
		if (type == ViewType.PERSP){ 
			PVector cartesianOffSet = Util.sphereToCart(distance,rotationI, rotationA);
			origin = PVector.add(translation, cartesianOffSet);
			GLU.gluLookAt(origin.x,origin.y,origin.z,translation.x,translation.y,translation.z,0,0,1);
		}
		else {
			glTranslatef(translation.x,translation.y,translation.z);
		}
		
		glScalef(scaleFactor,scaleFactor * ( flipped ? -1 : 1),scaleFactor);
		
		glEnable(GL_DEPTH_TEST);
		
		//Render the grid first, because it should always face the camera
		renderGrid();
		
		//Then perform any rotations to correctly show the axes and geometry
		glRotatef(type.rotationX,1,0,0);
		glRotatef(type.rotationY,0,1,0);
		glRotatef(type.rotationZ,0,0,1);
		
		cameraBuffer.update();
		
		renderAxes();
		renderGeometry();

		glDisable(GL_DEPTH_TEST);
		
		glPopMatrix(); // Pops the ModelView Matrix
		glMatrixMode(GL_PROJECTION);
		glPopMatrix(); // Pops the Projection Matrix
		
		//TODO There may be a better way to do this (resetting the view);
		glOrtho(0,Display.getWidth(),0,Display.getHeight(),-1,1);
		glViewport(0,0,Display.getWidth(),Display.getHeight());
		
		glPopMatrix();
	}
	
	private void renderAxes() {
		glColor3f(1,0,0);
		glBegin(GL_LINES);
			glVertex3f(0,0,0);
			glVertex3f(100,0,0);
		glEnd();
		
		glColor3f(0,1,0);
		glBegin(GL_LINES);
			glVertex3f(0,0,0);
			glVertex3f(0,100,0);
		glEnd();
		
		glColor3f(0,0,1);
		glBegin(GL_LINES);
			glVertex3f(0,0,0);
			glVertex3f(0,0,100);
		glEnd();
	}
	
	private void renderGrid() {
		glColor3f(0.7f,0.7f,0.7f);
		glBegin(GL_LINES);
		for (int i = -100; i<=100; i+= 10) {
			glVertex3f(i,-100,0);
			glVertex3f(i,100,0);
			glVertex3f(-100,i,0);
			glVertex3f(100,i,0);
		}
		glEnd();
	}
	
	private void renderGeometry() {
		if (geometry != null) geometry.render();
	}
	
	public void cycle() {
		changeType(type.getNext());
	}
	
	public void changeType(ViewType newType) {
		this.type = newType;
		parent.windowTitle = type.name;
		translation = new PVector(type.translationX,type.translationY,type.translationZ);
	}
	
	public void rotate(int dx, int dy) {
		if (type != ViewType.PERSP) return;
		
		rotationA += (float)dx / 50;
		rotationI -= (float)dy / 50;
		if (rotationI < 0) rotationI = 0;
		if (rotationI > Util.PI) rotationI = Util.PI;
	}
	
	public void pan(int dx,int dy) {
		if (type == ViewType.PERSP) {
			if (dx != 0) {
				float dA = 0;
				if (dx < 0) dA = Util.HALF_PI;
				else if (dx > 0) dA = -Util.HALF_PI;
				PVector azimuthAngle = Util.sphereToCart(1,Util.HALF_PI, rotationA + dA);
				translation.add(azimuthAngle);
			}
			if (dy != 0) {
				float dI = 0;
				if (dy < 0) dI = -Util.HALF_PI;
				else if (dy > 0) dI = Util.HALF_PI;
				PVector azimuthAngle = Util.sphereToCart(1,rotationI + dI, rotationA);
				translation.add(azimuthAngle);
			}	
		}
		else {
			translation.x += dx;
			translation.y += dy;
		}
		
	}

	@Override
	public void keyPressed() {
		if (Keyboard.getEventKey() == Keyboard.KEY_TAB) cycle();	
	}
	
	public void mousePressed() {
		
	}

	public void mouseDragged() {
		if (Mouse.isButtonDown(1)) {
			if (type == ViewType.PERSP){ 
				if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) pan(Mouse.getDX(),Mouse.getDY());
				else rotate(-Mouse.getDX(),-Mouse.getDY());
			}
			else pan(Mouse.getDX(),Mouse.getDY());
			
		}
				
	}

	@Override
	public void mouseWheel(float amt) {
		scaleFactor += amt / 100 * scaleFactor;
		if (scaleFactor < 0.01) scaleFactor = 0.01f;
	}
	
	public PVector screenToGround(int mouseX,int mouseY) {
		PVector near = cameraBuffer.unproject(mouseX,mouseY,0);
		PVector far  = cameraBuffer.unproject(mouseX,mouseY,1);

		far.sub(near);
		far.normalize();
		System.out.println(far);
		float f=Math.abs(origin.z/far.z);
		
		far.mult(f);
		far.add(origin);
		far.z=0;

		return(far);
	}

}
