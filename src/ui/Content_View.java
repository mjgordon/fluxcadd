package ui;



import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import org.lwjgl.util.glu.GLU;

import utility.PVector;
import utility.Util;

import static org.lwjgl.opengl.GL11.*;
public class Content_View extends WindowContent {
	
	ViewType type;
	
	//X and Y defines the translation in 2d mode. XYZ define the camera origin in 3d mode
	public PVector translation;
	
	//Defines the camera's inclination and azimuth in 3d mode
	public float rotationI = Util.PI * 5 / 4;
	public float rotationA = 0.377f;
	
	
	
	
	public Content_View(Window parent, ViewType type) {
		this.type = type;
		this.parent = parent;
		parent.windowTitle = type.name;
		
		translation = new PVector(type.translationX,type.translationY,type.translationZ);
	}

	public void render() {
		glColor3f(0,0,0);
		glViewport(parent.x,parent.y,parent.width,parent.height);
		
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glPushMatrix();
		
		if (type == ViewType.PERSP) {
			float aspect = (float)Display.getWidth()/Display.getHeight();
			GLU.gluPerspective(90, aspect, 0.01f,255);
		}
		else glOrtho(0,parent.width,0,parent.height, -100,100);
		
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glPushMatrix();
		
		if (type == ViewType.PERSP){ 
			PVector cartesianAngle = Util.sphereToCart(1,rotationI, rotationA);
			PVector center = PVector.add(translation, cartesianAngle);
			GLU.gluLookAt(translation.x,translation.y,translation.z,center.x,center.y,center.z,0,0,1);
		}
		else {
			glTranslatef(translation.x,translation.y,translation.z);
		}
		
		
		
		glRotatef(type.rotationX,1,0,0);
		glRotatef(type.rotationY,0,1,0);
		glRotatef(type.rotationZ,0,0,1);
		
		renderGrid();
		renderAxes();
		
		
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
		
		glOrtho(0,Display.getWidth(),0,Display.getHeight(),-1,1);
		glViewport(0,0,Display.getWidth(),Display.getHeight());
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
		glColor3f(1,1,1);
		glBegin(GL_LINES);
		for (int i = -100; i<=100; i+= 10) {
			glVertex3f(i,-100,0);
			glVertex3f(i,100,0);
			glVertex3f(-100,i,0);
			glVertex3f(100,i,0);
		}
		glEnd();
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
		
		rotationA += (float)dx / 30;
		rotationI -= (float)dy / 30;
	}
	
	public void pan(int dx,int dy) {
		if (type == ViewType.PERSP) {
			if (dx != 0) {
				float dA = 0;
				if (dx < 0) dA = -Util.HALF_PI;
				else if (dx > 0) dA = Util.HALF_PI;
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
		cycle();	
	}

	public void mouseDragged() {
		if (Mouse.isButtonDown(1)) {
			rotate(Mouse.getDX(),Mouse.getDY());
		}
				
	}

	@Override
	public void mouseWheel(float amt) {
		// TODO Auto-generated method stub
		
	}

}
