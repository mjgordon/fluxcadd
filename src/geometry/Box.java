package geometry;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;
import utility.Color;
import utility.PMatrix3D;
import utility.PVector;
import utility.Util;


public class Box extends Geometry {
	
	protected PVector[] explicitVertices = new PVector[8];

	public Box(PMatrix3D frame) {
		this.frame = frame;
		recalculateExplicitGeometry();
	}
	
	public Box(float x, float y, float z, float w, float l, float h, float azimuth) {
		PVector basisX = Util.sphereToCart(w / 2, Util.HALF_PI, azimuth);
		PVector basisY = Util.sphereToCart(l / 2, Util.HALF_PI, azimuth + Util.HALF_PI);
		
		PVector basisZ = basisX.cross(basisY);
		basisZ.setMag(h / 2);

		/* @formatter:off*/
		frame = new PMatrix3D(basisX.x, basisY.x, basisZ.x, x, 
				              basisX.y, basisY.y, basisZ.y, y, 
				              basisX.z, basisY.z, basisZ.z, z, 
				              0,        0,        0,        1);
		/* @formatter:on*/
		
		recalculateExplicitGeometry();
		this.colorFill = new Color(255, 255, 255);
	}
	
	public Box(float x, float y, float z, float w, float l, float h, float azimuth, float inclination) {
		PVector basisX = Util.sphereToCart(w / 2, Util.HALF_PI - inclination, azimuth);
		PVector basisY = Util.sphereToCart(l / 2, Util.HALF_PI, azimuth + Util.HALF_PI);
		
		
		PVector basisZ = basisX.cross(basisY);
		basisZ.setMag(h / 2);

		/* @formatter:off*/
		frame = new PMatrix3D(basisX.x, basisY.x, basisZ.x, x, 
				              basisX.y, basisY.y, basisZ.y, y, 
				              basisX.z, basisY.z, basisZ.z, z, 
				              0,        0,        0,        1);
		/* @formatter:on*/
		
		recalculateExplicitGeometry();
		this.colorFill = new Color(255, 255, 255);
	}
	
	public void render() {
		if (!visible) {
			return;
		}
		
		
		OGLWrapper.glColor(colorFill);
		
		if (colorFill != null) {
			GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
			OGLWrapper.glVertex(explicitVertices[0]);
			OGLWrapper.glVertex(explicitVertices[4]);
			OGLWrapper.glVertex(explicitVertices[1]);
			OGLWrapper.glVertex(explicitVertices[5]);
			OGLWrapper.glVertex(explicitVertices[2]);
			OGLWrapper.glVertex(explicitVertices[6]);
			OGLWrapper.glVertex(explicitVertices[3]);
			OGLWrapper.glVertex(explicitVertices[7]);
			OGLWrapper.glVertex(explicitVertices[0]);
			OGLWrapper.glVertex(explicitVertices[4]);
			GL11.glEnd();
			
			GL11.glBegin(GL11.GL_QUADS);
			OGLWrapper.glVertex(explicitVertices[0]);
			OGLWrapper.glVertex(explicitVertices[1]);
			OGLWrapper.glVertex(explicitVertices[2]);
			OGLWrapper.glVertex(explicitVertices[3]);
			
			OGLWrapper.glVertex(explicitVertices[4]);
			OGLWrapper.glVertex(explicitVertices[5]);
			OGLWrapper.glVertex(explicitVertices[6]);
			OGLWrapper.glVertex(explicitVertices[7]);
			
			GL11.glEnd();
		}
		
		GL11.glColor3f(0,0,0);
		
		//Upper Horizontals
		GL11.glBegin(GL11.GL_LINE_LOOP);
		
		OGLWrapper.glVertex(explicitVertices[0]);
		OGLWrapper.glVertex(explicitVertices[1]);
		OGLWrapper.glVertex(explicitVertices[2]);
		OGLWrapper.glVertex(explicitVertices[3]);
		
		GL11.glEnd();
		
		//Lower Horizontals
		GL11.glBegin(GL11.GL_LINE_LOOP);
		
		OGLWrapper.glVertex(explicitVertices[4]);
		OGLWrapper.glVertex(explicitVertices[5]);
		OGLWrapper.glVertex(explicitVertices[6]);
		OGLWrapper.glVertex(explicitVertices[7]);
		
		GL11.glEnd();
		
		//Verticals
		GL11.glBegin(GL11.GL_LINES);
		
		OGLWrapper.glVertex(explicitVertices[0]);
		OGLWrapper.glVertex(explicitVertices[4]);
		
		OGLWrapper.glVertex(explicitVertices[1]);
		OGLWrapper.glVertex(explicitVertices[5]);
		
		OGLWrapper.glVertex(explicitVertices[2]);
		OGLWrapper.glVertex(explicitVertices[6]);
		
		OGLWrapper.glVertex(explicitVertices[3]);
		OGLWrapper.glVertex(explicitVertices[7]);	
				
		GL11.glEnd();
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
	 * | 4----|-5   +X
	 * | |    | |    \
	 * 3------2 |  -Y-.-+Y
	 *  \|     \|      \
	 *   0------1      -X
	 * 
	 * 
	 */
	public void recalculateExplicitGeometry() {	
//		explicitVertices[0] = frame.mult(new PVector(-1,-1,-1), null);
//		explicitVertices[1] = frame.mult(new PVector( 1,-1,-1), null);
//		explicitVertices[2] = frame.mult(new PVector( 1, 1,-1), null);
//		explicitVertices[3] = frame.mult(new PVector(-1, 1,-1), null);
//
//		explicitVertices[4] = frame.mult(new PVector(-1,-1, 1), null);
//		explicitVertices[5] = frame.mult(new PVector( 1,-1, 1), null);
//		explicitVertices[6] = frame.mult(new PVector( 1, 1, 1), null);
//		explicitVertices[7] = frame.mult(new PVector(-1, 1, 1), null);
		
		explicitVertices[0] = frame.mult(new PVector(-1,-1,-1), null);
		explicitVertices[1] = frame.mult(new PVector(-1, 1,-1), null);
		explicitVertices[2] = frame.mult(new PVector( 1, 1,-1), null);
		explicitVertices[3] = frame.mult(new PVector( 1,-1,-1), null);

		explicitVertices[4] = frame.mult(new PVector(-1,-1, 1), null);
		explicitVertices[5] = frame.mult(new PVector(-1, 1, 1), null);
		explicitVertices[6] = frame.mult(new PVector( 1, 1, 1), null);
		explicitVertices[7] = frame.mult(new PVector( 1,-1, 1), null);
		
		
	}
	
	public float getLongestEdge() {
		PVector basisX = frame.getXBasis();
		PVector basisY = frame.getYBasis();
		PVector basisZ = frame.getZBasis();
	
		return(Math.max(basisX.mag(), Math.max(basisY.mag(), basisZ.mag())));
	}

	@Override
	public Intersection intersectLine(PVector start, PVector end) {
		// TODO Auto-generated method stub
		return null;
	}
}
