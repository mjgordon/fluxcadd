package geometry;

import static org.lwjgl.opengl.GL11.*;

import utility.PVector;
import utility.Vector6;

public class Box extends Geometry {
	public PVector size;
	public PVector offset;

	public Box(Vector6 v) {
		size = v.getXYZ();
		offset = v.getABC();
	}
	
	
	public void render() {
		if (!visible) return;
		
		glColor3f(r,g,b);
		
		//Upper Horizontals
		glBegin(GL_LINE_LOOP);
		glVertex3f(size.x/2,size.y/2,size.z);
		glVertex3f(-size.x/2,size.y/2,size.z);
		glVertex3f(-size.x/2,-size.y/2,size.z);
		glVertex3f(size.x/2,-size.y/2,size.z);
		glEnd();
		
		//Lower Horizontals
		glBegin(GL_LINE_LOOP);
		glVertex3f(size.x/2,size.y/2,0);
		glVertex3f(-size.x/2,size.y/2,0);
		glVertex3f(-size.x/2,-size.y/2,0);
		glVertex3f(size.x/2,-size.y/2,0);
		glEnd();
		
		//Verticals
		glBegin(GL_LINES);
		glVertex3f(size.x/2,size.y/2,size.z);
		glVertex3f(size.x/2,size.y/2,0);
		
		glVertex3f(-size.x/2,size.y/2,size.z);
		glVertex3f(-size.x/2,size.y/2,0);
		
		glVertex3f(size.x/2,-size.y/2,size.z);
		glVertex3f(size.x/2,-size.y/2,0);
		
		glVertex3f(-size.x/2,-size.y/2,size.z);
		glVertex3f(-size.x/2,-size.y/2,0);
			
		glEnd();
		
	}
}
