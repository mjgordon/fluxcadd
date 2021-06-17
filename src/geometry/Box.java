package geometry;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import utility.Color;
import utility.PMatrix3D;
import utility.PVector;



public class Box extends Geometry {
	
	private PVector[] explicitVertices = new PVector[8];

	public Box(PMatrix3D frame) {
		this.frame = frame;
	}
	
	
	public void render() {
		if (!visible) return;
		
		Color.setGlColor(color);
		
		//Upper Horizontals
		glBegin(GL_LINE_LOOP);
		glVertex3f(explicitVertices[0].x,explicitVertices[0].y,explicitVertices[0].z);
		glVertex3f(explicitVertices[1].x,explicitVertices[1].y,explicitVertices[1].z);
		glVertex3f(explicitVertices[2].x,explicitVertices[2].y,explicitVertices[2].z);
		glVertex3f(explicitVertices[3].x,explicitVertices[3].y,explicitVertices[3].z);
		glEnd();
		
		//Lower Horizontals
		glBegin(GL_LINE_LOOP);
		glVertex3f(explicitVertices[4].x,explicitVertices[4].y,explicitVertices[4].z);
		glVertex3f(explicitVertices[5].x,explicitVertices[5].y,explicitVertices[5].z);
		glVertex3f(explicitVertices[6].x,explicitVertices[6].y,explicitVertices[6].z);
		glVertex3f(explicitVertices[7].x,explicitVertices[7].y,explicitVertices[7].z);
		glEnd();
		
		//Verticals
		glBegin(GL_LINES);
		glVertex3f(explicitVertices[0].x,explicitVertices[0].y,explicitVertices[0].z);
		glVertex3f(explicitVertices[4].x,explicitVertices[4].y,explicitVertices[4].z);
		
		glVertex3f(explicitVertices[1].x,explicitVertices[1].y,explicitVertices[1].z);
		glVertex3f(explicitVertices[5].x,explicitVertices[5].y,explicitVertices[5].z);
		
		glVertex3f(explicitVertices[2].x,explicitVertices[2].y,explicitVertices[2].z);
		glVertex3f(explicitVertices[6].x,explicitVertices[6].y,explicitVertices[6].z);
		
		glVertex3f(explicitVertices[3].x,explicitVertices[3].y,explicitVertices[3].z);
		glVertex3f(explicitVertices[7].x,explicitVertices[7].y,explicitVertices[7].z);
		glEnd();
	}
	
	

	//TODO: FEATURE : getPointRepresentation implementation

	@Override
	public PVector[] getVectorRepresentation(float resolution) {
		return new PVector[0];
	}
	
	//TODO : FEATURE : getHatchLines implementation
	@Override
	public ArrayList<Line> getHatchLines() {
		return(new ArrayList<Line>());
	}
	
	@Override
	/**
	 * Order of vertices
	 * 
	 * 7------6
	 * |\     |\
	 * | 4----|-5
	 * | |    | |
	 * 3------2 |
	 *  \|     \|
	 *   0------1
	 * 
	 * 
	 */
	public void recalculateExplicitGeometry() {
		PVector position = frame.getPositionVector();
		PVector basisX = PVector.div(frame.getXBasis(), 2);
		PVector basisY = PVector.div(frame.getYBasis(), 2);
		PVector basisZ = PVector.div(frame.getZBasis(), 2);
		
		
		explicitVertices[0] = new PVector(position.x - basisX.x, position.y - basisY.y, position.z - basisZ.z);
		explicitVertices[1] = new PVector(position.x + basisX.x, position.y - basisY.y, position.z - basisZ.z);
		explicitVertices[2] = new PVector(position.x + basisX.x, position.y + basisY.y, position.z - basisZ.z);
		explicitVertices[3] = new PVector(position.x - basisX.x, position.y + basisY.y, position.z - basisZ.z);
		
		explicitVertices[4] = new PVector(position.x - basisX.x, position.y - basisY.y, position.z + basisZ.z);
		explicitVertices[5] = new PVector(position.x + basisX.x, position.y - basisY.y, position.z + basisZ.z);
		explicitVertices[6] = new PVector(position.x + basisX.x, position.y + basisY.y, position.z + basisZ.z);
		explicitVertices[7] = new PVector(position.x - basisX.x, position.y + basisY.y, position.z + basisZ.z);
	}
	
	public float getLongestEdge() {
		PVector basisX = frame.getXBasis();
		PVector basisY = frame.getYBasis();
		PVector basisZ = frame.getZBasis();
	
		return(Math.max(basisX.mag(), Math.max(basisY.mag(), basisZ.mag())));
	}
}
