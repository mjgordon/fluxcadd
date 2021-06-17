package geometry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import utility.Color;
import utility.PMatrix3D;
import utility.PVector;
import static org.lwjgl.opengl.GL11.*;

public class OBJModel extends Geometry {
	public ArrayList<String> file;
	public ArrayList<PVector> vertices;
	public ArrayList<PVector> vertexNormals;
	public ArrayList<Polygon> polygons;

	public int graphicSetting;
	public static final int VISIBLE = 0;
	public static final int GHOSTED = 1;
	public static final int INVISIBLE = 2;

	public OBJModel(String name) {
		super();
		vertices = new ArrayList<PVector>();
		vertexNormals = new ArrayList<PVector>();
		polygons = new ArrayList<Polygon>();

		String path = "objs/" + name;

		file = new ArrayList<String>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			while (line != null) {
				file.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}

		for (String s : file) {
			String[] parts = s.split(" ");
			if (parts[0].equals("v")) {
				float x = Float.valueOf(parts[1]);
				float y = Float.valueOf(parts[2]);
				float z = Float.valueOf(parts[3]);
				PVector vertex = new PVector(x, y, z);
				vertices.add(vertex);
			} else if (parts[0].equals("vn")) {
				float x = Float.valueOf(parts[1]);
				float y = Float.valueOf(parts[2]);
				float z = Float.valueOf(parts[3]);
				PVector vertexNormal = new PVector(x, y, z);
				vertexNormals.add(vertexNormal);
			} else if (parts[0].equals("f")) {
				Polygon polygon = new Polygon();
				for (int i = 1; i < parts.length; i++) {
					if (parts[i].indexOf("/") != -1) {
						String[] polygonParts = parts[i].split("/");
						polygon.vertexIds.add(Integer.valueOf(polygonParts[0]) - 1);
						polygon.vertexNormalIds.add(Integer.valueOf(polygonParts[2]) - 1);
					} else {
						polygon.vertexIds.add(Integer.valueOf(parts[i]) - 1);
					}
				}
				polygons.add(polygon);
			}
		}
	}

	public void render() {
		if (!visible)
			return;
		if (graphicSetting == INVISIBLE)
			return;

		glEnable(GL_POLYGON_OFFSET_FILL);
		glPolygonOffset(1, 1);
		glEnable(GL_DEPTH_TEST);
		for (Polygon polygon : polygons) {
			glPushMatrix();

			Color.setGlColor(color, (graphicSetting == VISIBLE) ? 255 : 127);
			glBegin(GL_POLYGON);
			for (int i = 0; i < polygon.vertexIds.size(); i++) {
				float vx = vertices.get(polygon.vertexIds.get(i)).x;
				float vy = vertices.get(polygon.vertexIds.get(i)).y;
				float vz = vertices.get(polygon.vertexIds.get(i)).z;

				if (polygon.vertexNormalIds.size() > 0) {
					float nx = vertices.get(polygon.vertexNormalIds.get(i)).x;
					float ny = vertices.get(polygon.vertexNormalIds.get(i)).y;
					float nz = vertices.get(polygon.vertexNormalIds.get(i)).z;
					glNormal3f(nx, ny, nz);
				}

				glVertex3f(vx, vy, vz);
			}
			glEnd();

			glColor3f(0.5f, 0.5f, 0.5f);
			glBegin(GL_LINE_LOOP);
			for (int i = 0; i < polygon.vertexIds.size(); i++) {
				float vx = vertices.get(polygon.vertexIds.get(i)).x;
				float vy = vertices.get(polygon.vertexIds.get(i)).y;
				float vz = vertices.get(polygon.vertexIds.get(i)).z;

				if (polygon.vertexNormalIds.size() > 0) {
					float nx = vertices.get(polygon.vertexNormalIds.get(i)).x;
					float ny = vertices.get(polygon.vertexNormalIds.get(i)).y;
					float nz = vertices.get(polygon.vertexNormalIds.get(i)).z;
					glNormal3f(nx, ny, nz);
				}

				glVertex3f(vx, vy, vz);
			}
			glEnd();

			glPopMatrix();
		}
		glDisable(GL_LIGHTING);
	}

	public Box getBoundingBox() {
		float minX = Float.MAX_VALUE;
		float minY = Float.MAX_VALUE;
		float minZ = Float.MAX_VALUE;
		float maxX = -Float.MAX_VALUE;
		float maxY = -Float.MAX_VALUE;
		float maxZ = -Float.MAX_VALUE;

		for (PVector v : vertices) {
			if (v.x < minX)
				minX = v.x;
			if (v.y < minY)
				minY = v.y;
			if (v.z < minZ)
				minZ = v.z;
			if (v.x > maxX)
				maxX = v.x;
			if (v.y > maxY)
				maxY = v.y;
			if (v.z > maxZ)
				maxZ = v.z;
		}

		PVector size = new PVector(maxX - minX, maxY - minY, maxZ - minZ);
		
		PMatrix3D boxFrame = new PMatrix3D();
		boxFrame.m03 = minX - size.x / 2;
		boxFrame.m13 = minY - size.y / 2;
		boxFrame.m23 = minZ - size.z / 2;
		boxFrame.m00 = size.x;
		boxFrame.m11 = size.y;
		boxFrame.m22 = size.z;
		
		return (new Box(boxFrame));
	}

	public void scale(float scaleFactor) {
		for (PVector v : vertices)
			v.mult(scaleFactor);
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
			} else {
				out.add(new Line(vertices.get(vertexIds.get(2)), vertices.get(vertexIds.get(3))));
				out.add(new Line(vertices.get(vertexIds.get(3)), vertices.get(vertexIds.get(0))));
			}
			return (out);
		}
	}

	@Override
	public void recalculateExplicitGeometry() {
		// TODO Auto-generated method stub
		
	}
}
