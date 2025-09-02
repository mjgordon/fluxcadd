package geometry;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import graphics.ImageLoader;
import intersection.Intersection;

public class Mesh extends Geometry {

	public ArrayList<Vector3d> vertices;
	public ArrayList<Vector3d> vertexNormals;
	public ArrayList<Vector2d> vertexTextures;
	public ArrayList<Polygon> polygons;

	public int graphicSetting;
	
	// TODO: Revisit the use of these
	public static final int VISIBLE = 0;
	public static final int GHOSTED = 1;
	public static final int INVISIBLE = 2;

	private Box boundingBox;
	
	public boolean wireframe = true;
	
	private int glidTexture = 0;
	
	
	public Mesh() {
		super();
		vertices = new ArrayList<Vector3d>();
		vertexNormals = new ArrayList<Vector3d>();
		vertexTextures = new ArrayList<Vector2d>();
		polygons = new ArrayList<Polygon>();
	}


	@SuppressWarnings("static-access")
	public void render(double time) {
		if (!visible) {
			return;
		}

		if (graphicSetting == INVISIBLE) {
			return;
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		if (glidTexture != 0 && !wireframe) {
			GL33.glEnable(GL33.GL_TEXTURE_2D);
			GL33.glBindTexture(GL33.GL_TEXTURE_2D,  glidTexture);
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL);
		}
		else {
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);
		}
		
		GL33.glMatrixMode(GL33.GL_MODELVIEW);
		GL33.glPushMatrix();
		
		GL11.glMultMatrixd(modelMatrix.getArray(time));

		for (Polygon polygon : polygons) {
			if (wireframe || glidTexture == 0) {
				GL11.glColor3f(0.5f, 0.5f, 0.5f);
				GL11.glBegin(GL11.GL_LINE_LOOP);	
				traversePolygon(polygon);
				GL11.glEnd();	
			}
			else {
				GL11.glColor3f(colorFill.r, colorFill.g, colorFill.b);
				if (polygon.vertexIds.size() == 3) {
					GL11.glBegin(GL11.GL_TRIANGLES);
				}
				else {
					GL11.glBegin(GL11.GL_QUADS);
				}
				traversePolygon(polygon);
				GL11.glEnd();				
			}
		}
		
		GL33.glPopMatrix();
		GL33.glDisable(GL33.GL_TEXTURE_2D);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}


	@SuppressWarnings("static-access")
	private void traversePolygon(Polygon polygon) {
		for (int i = 0; i < polygon.vertexIds.size(); i++) {
			if (polygon.vertexNormalIds.size() > 0) {
				Vector3d vertexNormal = vertexNormals.get(polygon.vertexNormalIds.get(i));
				GL11.glNormal3d(vertexNormal.x, vertexNormal.y, vertexNormal.z);
			}
			if (polygon.vertexTextureIds.size() > 0) {
				Vector2d texCoor = vertexTextures.get(polygon.vertexTextureIds.get(i));
				GL33.glTexCoord2d(texCoor.x, texCoor.y);
			}
			Vector3d vertex = vertices.get(polygon.vertexIds.get(i));
			GL33.glVertex3d(vertex.x, vertex.y, vertex.z);
		}
	}


	public Box getBoundingBox() {
		return this.boundingBox;
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
		public ArrayList<Integer> vertexTextureIds = new ArrayList<Integer>();


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
	
	
	@SuppressWarnings("static-access")
	public void loadTexture(String filepath) {
		if (glidTexture != 0) {
			// At some point texture may be shared so this'll need a redo
			GL33.glDeleteTextures(glidTexture);
			glidTexture = 0;
		}
		
		ImageLoader loader = new ImageLoader(filepath);
		ByteBuffer imageBuffer = loader.buffer;
		
		glidTexture = GL33.glGenTextures();
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, glidTexture);
		
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_REPEAT);	
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_REPEAT);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR_MIPMAP_LINEAR);
		GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
		
		
		GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, loader.width, loader.height, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, imageBuffer);
		GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);
		
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
	}
}
