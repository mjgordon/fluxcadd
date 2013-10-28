package geometry;

import static org.lwjgl.opengl.GL11.*;
import utility.PVector;

public class Line extends Geometry {

	PVector startPoint;
	PVector endPoint;
	
	public Line(PVector startPoint, PVector endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}
	
	
	
	public void render() {
		glColor3f(r,g,b);
		glBegin(GL_LINES);
		glVertex3f(startPoint.x,startPoint.y,startPoint.z);
		glVertex3f(endPoint.x,endPoint.y,endPoint.z);
		glEnd();
		
	}

}
