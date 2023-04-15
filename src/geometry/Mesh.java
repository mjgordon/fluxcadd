package geometry;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;

public class Mesh extends Geometry {

	public ArrayList<Vector3d> vertices;
	public ArrayList<Vector3d> vertexNormals;
	public ArrayList<Polygon> polygons;

	public int graphicSetting;
	public static final int VISIBLE = 0;
	public static final int GHOSTED = 1;
	public static final int INVISIBLE = 2;

	private Box boundingBox;


	public Mesh() {
		super();
		vertices = new ArrayList<Vector3d>();
		vertexNormals = new ArrayList<Vector3d>();
		polygons = new ArrayList<Polygon>();
	}


	public void render(double time) {
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


	public void scale(double scaleFactor) {
		for (Vector3d v : vertices) {
			v.mul(scaleFactor);
		}

		recalculateExplicitGeometry();
	}


	// TODO : FEATURE : getVectorRepresentation implementation
	@Override
	public Vector3d[] getVectorRepresentation(double resolution) {
		return new Vector3d[0];
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
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = -Double.MAX_VALUE;
		double maxY = -Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;

		for (Vector3d v : vertices) {
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

		Vector3d size = new Vector3d(maxX - minX, maxY - minY, maxZ - minZ);

		Matrix4d boxFrame = new Matrix4d();
		boxFrame.m03(maxX - (size.x / 2));
		boxFrame.m13(maxY - (size.y / 2));
		boxFrame.m23(maxZ - (size.z / 2));
		boxFrame.m00(size.x);
		boxFrame.m11(size.y);
		boxFrame.m22(size.z);

		System.out.println(boxFrame.m03() + " : " + boxFrame.m13() + " : " + boxFrame.m23());
		System.out.println(boxFrame.m00() + " : " + boxFrame.m11() + " : " + boxFrame.m22());

		this.boundingBox = new Box(boxFrame);

	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}
}
