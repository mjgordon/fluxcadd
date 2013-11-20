package geometry;

import static org.lwjgl.opengl.GL11.*;
import utility.PVector;

public class Line extends Geometry {

	public PVector startPoint;
	public PVector endPoint;
	
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
	
	public float getM() {
		float dy = endPoint.y - startPoint.y;
		float dx = endPoint.x - startPoint.x;
		
		return(dy / dx);
	}
	
	public float xIntersect(float y) {
		float m = getM();
		float dy = startPoint.y - y;
		float f = dy / m;
		return(startPoint.x - f);
	}
	
	public boolean containsX(float x) {
		return( (x >= startPoint.x && x <= endPoint.x) || (x >= endPoint.x && x <= startPoint.x) );
	}
	

}
