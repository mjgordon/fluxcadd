package ui;



import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import org.lwjgl.util.glu.GLU;

import static org.lwjgl.opengl.GL11.*;
public class Content_View extends WindowContent {
	
	ViewType type;
	
	public Content_View(Window parent, ViewType type) {
		this.type = type;
		this.parent = parent;
		parent.windowTitle = type.name;
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
			GLU.gluLookAt(0,20,0,0,0,0,0,0,1);
		}
		else {
			glTranslatef(130,130,0);
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
	}
	

	@Override
	public void keyPressed() {
		cycle();
		
	}



	@Override
	public void mouseWheel(float amt) {
		// TODO Auto-generated method stub
		
	}

}
