package geometry;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector2i;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import graphics.Graphics;
import graphics.Graphics3D;
import graphics.ImageLoader;
import intersection.Intersection;

public class Mesh extends Geometry {

	public Vector3d[] vertices;
	public Vector3d[] vertexNormals;
	public Vector2d[] vertexTextures;
	public ArrayList<Polygon> polygons;

	private Box boundingBox;
	
	public boolean wireframe = true;
	
	private int glidTexture = 0;
	
	private int triCount = 0;
	
	
	public Mesh() {
		super();
		vertices = null;
		vertexNormals = null;
		vertexTextures = null;
		polygons = new ArrayList<Polygon>();
	}


	@SuppressWarnings("static-access")
	public void render(double time) {
		if (!visible) {
			return;
		}

		GL33.glEnable(GL33.GL_DEPTH_TEST);
		
		if (wireframe) {
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Normal	
		}
		else {
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL);
			GL33.glEnable(GL33.GL_TEXTURE_2D);
			GL33.glBindTexture(GL33.GL_TEXTURE_2D,  glidTexture);	
		}
		
		
		Graphics3D.shaderTextured.use();
		
		Graphics3D.shaderTextured.setMatrix4("model", this.modelMatrix.get(time));
		Graphics3D.shaderTextured.setMatrix4("view", Graphics3D.view);
		Graphics3D.shaderTextured.setMatrix4("projection", Graphics3D.projection);
		
		GL33.glBindVertexArray(glidVAO);
		
		GL33.glDrawElements(GL33.GL_TRIANGLES, triCount * 3, GL33.GL_UNSIGNED_INT, 0);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
		GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
		GL33.glDisable(GL33.GL_TEXTURE_2D);
		GL33.glDisable(GL33.GL_DEPTH_TEST);
	}


	public Box getBoundingBox() {
		return this.boundingBox;
	}


	// TODO : FEATURE : getHatchLines implementation
	@Override
	public ArrayList<Line> getHatchLines() {
		return (new ArrayList<Line>());
	}


	public class Polygon {
		public int[] vertexIds = null;
		public int[] vertexNormalIds = null;
		public int[] vertexTextureIds = null;


		public ArrayList<Line> getLines() {
			ArrayList<Line> out = new ArrayList<Line>();
			out.add(new Line(vertices[vertexIds[0]], vertices[vertexIds[1]]));
			out.add(new Line(vertices[vertexIds[1]], vertices[vertexIds[2]]));
			if (vertexIds.length == 3) {
				out.add(new Line(vertices[vertexIds[2]], vertices[vertexIds[0]]));
			}
			else {
				out.add(new Line(vertices[vertexIds[2]], vertices[vertexIds[3]]));
				out.add(new Line(vertices[vertexIds[3]], vertices[vertexIds[0]]));
			}
			return (out);
		}
	}


	@SuppressWarnings("static-access")
	@Override
	public void setupVAO() {
		super.setupVAO();
		
		ArrayList<Vector2i> uniqueArray = new ArrayList<Vector2i>();
		ArrayList<Integer> uniqueIds = new ArrayList<Integer>();
		
		
		int[] quadIds = {0, 1, 3, 2, 3, 1};
		int[] triIds = {0, 1, 2};
		for (Polygon poly : polygons) {
			int[] ids = poly.vertexIds.length == 3 ? triIds : quadIds;
			
			triCount += poly.vertexIds.length == 3 ? 1 : 2;
			
			for (int i : ids) {
				Vector2i v = new Vector2i(poly.vertexIds[i], poly.vertexTextureIds[i]);
				
				int containsId = uniqueArray.indexOf(v);
				if (containsId == -1) {
					uniqueIds.add(uniqueArray.size());
					uniqueArray.add(v);
				}
				else {
					uniqueIds.add(containsId);
				}
			}
		}
		
		float[] vertexFloats = new float[uniqueArray.size() * 5];
		
		for (int i = 0; i < uniqueArray.size(); i++) {
			Vector3d pos = vertices[uniqueArray.get(i).x];
			Vector2d tex = vertexTextures[uniqueArray.get(i).y];
			
			vertexFloats[i * 5 + 0] = (float) pos.x;
			vertexFloats[i * 5 + 1] = (float) pos.y;
			vertexFloats[i * 5 + 2] = (float) pos.z;
			vertexFloats[i * 5 + 3] = (float) tex.x;
			vertexFloats[i * 5 + 4] = (float) tex.y;
		}
		
		int[] indices = uniqueIds.stream().mapToInt(i -> i).toArray();
		
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			glidVBO = Graphics.initVBO(stack, vertexFloats);
			glidEBO = Graphics.initEBO(stack, indices);

			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 20, 0);
			
			GL33.glEnableVertexAttribArray(1);
			GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 20, 12);
		}
		
		
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
