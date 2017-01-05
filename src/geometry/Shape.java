package geometry;

import java.util.ArrayList;

import utility.PVector;

import static org.lwjgl.opengl.GL11.*;
public class Shape extends Geometry {

	public float fillR;
	public float fillG;
	public float fillB;
	
	public boolean stroked = true;
	public boolean filled = false;
	
	public ArrayList<PVector> vertices;
	
	public Shape(ArrayList<PVector> vertices) {
		super();
		this.vertices = vertices;
	}
	
	public Shape(ArrayList<PVector> vertices, float fillR,float fillG, float fillB) {
		super();
		this.vertices = vertices;
		this.fillR = fillR;
		this.fillG = fillG;
		this.fillB = fillB;
	}
	
	@Override
	public void render() {
		if (!visible) return;
		if (filled) {
			glColor3f(fillR,fillG,fillB);
			glBegin(GL_POLYGON);
			for (PVector v : vertices) {
				glVertex2f(v.x,v.y);
			}
			glEnd();
		}
		
		if (stroked) {
			glColor3f(r,g,b);
			glBegin(GL_LINE_LOOP);
			for (PVector v : vertices) {
				glVertex2f(v.x,v.y);
			}
			glEnd();
		}
	}
	
}
