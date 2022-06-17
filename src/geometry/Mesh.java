package geometry;

import java.util.ArrayList;

import utility.PMatrix3D;
import utility.PVector;
import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;

public class Mesh extends Geometry {
	
	public ArrayList<PVector> vertices;
	public ArrayList<PVector> vertexNormals;
	public ArrayList<Polygon> polygons;

	public int graphicSetting;
	public static final int VISIBLE = 0;
	public static final int GHOSTED = 1;
	public static final int INVISIBLE = 2;
	
	private Box boundingBox;
	
	public Mesh() {
		super();
		vertices = new ArrayList<PVector>();
		vertexNormals = new ArrayList<PVector>();
		polygons = new ArrayList<Polygon>();
	}

	public void render() {
		if (!visible) {
			return;
		}

		if (graphicSetting == INVISIBLE) {
			return;
		}
		
		GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
		GL11.glPolygonOffset(1, 1);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		
		for (Polygon polygon : polygons) {
			GL11.glPushMatrix();

			OGLWrapper.glColor(colorFill, (graphicSetting == VISIBLE) ? 255 : 127);
			GL11.glBegin(GL11.GL_POLYGON);
			traversePolygon(polygon);
			GL11.glEnd();

			GL11.glColor3f(0.5f, 0.5f, 0.5f);
			GL11.glBegin(GL11.GL_LINE_LOOP);
			traversePolygon(polygon);
			GL11.glEnd();

			GL11.glPopMatrix();
		}
		GL11.glDisable(GL11.GL_LIGHTING);
	}
	
	private void traversePolygon(Polygon polygon) {
		for (int i = 0; i < polygon.vertexIds.size(); i++) {
			if (polygon.vertexNormalIds.size() > 0) {
				OGLWrapper.glNormal(vertexNormals.get(polygon.vertexNormalIds.get(i)));
			}
			OGLWrapper.glVertex(vertices.get(polygon.vertexIds.get(i)));
		}
	}

	public Box getBoundingBox() {
		return this.boundingBox;
	}

	public void scale(float scaleFactor) {
		for (PVector v : vertices) {
			v.mult(scaleFactor);
		}

		recalculateExplicitGeometry();
	}

	// TODO : FEATURE : getVectorRepresentation implementation
	@Override
	public PVector[] getVectorRepresentation(float resolution) {
		return new PVector[0];
	}

	// TODO : FEATURE : getHatchLines implementation
	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>());
	}

	public class Polygon {
		public ArrayList<Integer> vertexIds = new ArrayList<Integer>();
		public ArrayList<Integer> vertexNormalIds = new ArrayList<Integer>();

		public ArrayList<Line> getLines() {
			ArrayList<Line> out = new ArrayList<Line>();
			out.add(new Line(vertices.get(vertexIds.get(0)), vertices.get(vertexIds.get(1))));
			out.add(new Line(vertices.get(vertexIds.get(1)), vertices.get(vertexIds.get(2))));
			if (vertexIds.size() == 3) {
				out.add(new Line(vertices.get(vertexIds.get(2)), vertices.get(vertexIds.get(0))));
			}
			else {
				out.add(new Line(vertices.get(vertexIds.get(2)), vertices.get(vertexIds.get(3))));
				out.add(new Line(vertices.get(vertexIds.get(3)), vertices.get(vertexIds.get(0))));
			}
			return (out);
		}
	}

	@Override
	public void recalculateExplicitGeometry() {
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float minZ = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		float maxZ = -Float.MAX_VALUE;

		for (PVector v : vertices) {
			if (v.x < minX) {
				minX = v.x;
			}	
			if (v.y < minY) {
				minY = v.y;
			}	
			if (v.z < minZ) {
				minZ = v.z;
			}	
			if (v.x > maxX) {
				maxX = v.x;
			}	
			if (v.y > maxY) {
				maxY = v.y;
			}
			if (v.z > maxZ) {
				maxZ = v.z;
			}	
		}

		PVector size = new PVector(maxX - minX, maxY - minY, maxZ - minZ);

		PMatrix3D boxFrame = new PMatrix3D();
		boxFrame.m03 = maxX - (size.x / 2);
		boxFrame.m13 = maxY - (size.y / 2);
		boxFrame.m23 = maxZ - (size.z / 2);
		boxFrame.m00 = size.x;
		boxFrame.m11 = size.y;
		boxFrame.m22 = size.z;
		
		System.out.println(boxFrame.m03 + " : " + boxFrame.m13 + " : " + boxFrame.m23);
		System.out.println(boxFrame.m00 + " : " + boxFrame.m11 + " : " + boxFrame.m22);
		
		this.boundingBox = new Box(boxFrame);
		

	}

	@Override
	public Intersection intersectLine(PVector start, PVector end) {
		// TODO Auto-generated method stub
		return null;
	}
}
