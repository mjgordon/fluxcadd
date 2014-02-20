package geometry;

import static org.lwjgl.opengl.GL11.*;
import utility.PVector;

public class Line extends Geometry {

	public PVector startPoint;
	public PVector endPoint;
	public int width = 1;
	
	public Line(Point a, Point b) {
		this.startPoint = a.position;
		this.endPoint = b.position;
	}
	
	public Line(PVector startPoint, PVector endPoint) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}
	
	
	
	public void render() {
		glColor3f(r,g,b);
		glLineWidth(width);
		glBegin(GL_LINES);
		glVertex3f(startPoint.x,startPoint.y,startPoint.z);
		glVertex3f(endPoint.x,endPoint.y,endPoint.z);
		glEnd();
		glLineWidth(1);
		
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
	
	public PVector xyIntersect(float z) {
		PVector angle = PVector.sub(endPoint, startPoint);
		if (angle.z == 0) return null;
		angle.div(angle.z);
		float dz = z - startPoint.z; 
		angle.mult(dz);
		PVector intersect = PVector.add(startPoint,angle);
		if (endPoint.z > startPoint.z) {
			if (intersect.z >= startPoint.z && intersect.z <= endPoint.z){ 
				return(intersect);
			}
			else return(null);
		}
		else {
			if (intersect.z >= endPoint.z && intersect.z <= startPoint.z ) {
				return(intersect);
			}
			else return(null);
		}
		
	}
	
	public boolean containsX(float x) {
		return( (x >= startPoint.x && x <= endPoint.x) || (x >= endPoint.x && x <= startPoint.x) );
	}
	

}
