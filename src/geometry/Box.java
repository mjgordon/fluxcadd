package geometry;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import utility.Color;
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
		
		Color.setGlColor(color);
		
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
	
	

	//TODO: FEATURE : getPointRepresentation implementation

	@Override
	public ArrayList<PVector> getVectorRepresentation(float resolution) {
		return new ArrayList<PVector>();
	}
	
	//TODO : FEATURE : getHatchLines implementation
	@Override
	public ArrayList<Line> getHatchLines() {
		return(new ArrayList<Line>());
	}
}
