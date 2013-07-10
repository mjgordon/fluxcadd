package ui;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
public class Content_View extends WindowContent {
	
	ViewType type;
	
	public Content_View(Window parent, ViewType type) {
		this.type = type;
		this.parent = parent;
	}

	public void render() {
		glColor3f(0,0,0);
		glViewport(parent.x,parent.y,parent.width,parent.height);
		
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glPushMatrix();
		glOrtho(0,1000,0,1000, -1, 1);
		
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glPushMatrix();
		
		glTranslatef(Mouse.getX(),Mouse.getY(),0);
		
		renderAxes();
		glPopMatrix();
		glMatrixMode(GL_PROJECTION);
		glPopMatrix();
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
		
	}
	

	@Override
	public void keyPressed() {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseWheel(float amt) {
		// TODO Auto-generated method stub
		
	}

}
