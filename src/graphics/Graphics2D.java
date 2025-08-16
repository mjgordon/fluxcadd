package graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import utility.Color3i;

/**
 * For drawing screen-pixel space elements (e.g. GUI)
 * Maintains a matrix stack similar to fixed-pipeline OpenGL
 */
public class Graphics2D {
	
	protected static Shader shaderFilledRectangle;
	
	protected static int glidVAOFill;
	protected static int glidVAOStroke;
	
	public static MatrixStack stack;
	
	
	public static Color3i colorFill = null;
	public static Color3i colorStroke = null;
	
	
	public static void pushMatrix() {
		stack.push();
	}
	
	
	public static void popMatrix() {
		stack.pop();
	}
	
	
	public static void setOrtho(int width, int height) {
		stack.get().setOrtho(0, width, 0, height, -1, 1);
	}
	
	
	/**
	 * Called after calling glViewport to set pixel-space drawing, with the origin in the upper left
	 * @param width
	 * @param height
	 */
	public static void fitViewport(int width, int height) {
		setOrtho(width, height);
		stack.get().translate(0, height, 0);
		stack.get().scale(1, -1, 1);
	}
	
	
	public static void translate(float x, float y) {
		stack.get().translate(x, y, 0);
	}
	
	
	/**
	 * Sets the fill color, on a 0-255 scale.
	 */
	public static void fill(int r, int g, int b) {
		colorFill = new Color3i(r, g, b);
	}


	/**
	 * Sets the fill color from a single hex value
	 */
	public static void fill(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = (rgb) & 0xff;

		fill(r, g, b);
	}


	/**
	 * Sets the stroke color, on a 0-255 scale.
	 */
	public static void stroke(int r, int g, int b) {
		colorStroke = new Color3i(r, g, b);
	}


	/**
	 * Sets the stroke color from a single hex value
	 */
	public static void stroke(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = (rgb) & 0xff;

		stroke(r, g, b);
	}


	public static void noFill() {
		colorFill = null;
	}


	public static void noStroke() {
		colorStroke = null;
	}


	public static void rect(int x, int y, int width, int height) {
		Matrix4f projection = stack.get();
		
		if (colorFill != null) {
			rectInternal(projection, x, y, width, height, colorFill, true);
		}
		if (colorStroke != null) {
			rectInternal(projection, x, y, width, height, colorStroke, false);
		}
	}
	
	
	@SuppressWarnings("static-access")
	private static void rectInternal(Matrix4f projection, int x, int y, int width, int height, Color3i color, boolean filled) {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL33.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL33.glPushMatrix();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		shaderFilledRectangle.use();
		shaderFilledRectangle.setVec2("position", x, y);
		shaderFilledRectangle.setVec2("size", width, height);
		shaderFilledRectangle.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shaderFilledRectangle.setMatrix4("projection", projection);

		if (filled) {
			GL33.glBindVertexArray(glidVAOFill);
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL); // Normal	
			GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0);
		}
		else {
			GL33.glBindVertexArray(glidVAOStroke);
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
			GL33.glDrawElements(GL33.GL_LINE_LOOP, 6, GL33.GL_UNSIGNED_INT, 0);
		}
			
		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL33.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL33.glPopMatrix();
		
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL); // Normal	
	}
	
	
	@Deprecated
	public static void rect2(int x, int y, int width, int height) {
		if (OGLWrapper.colorFill != null) {
			OGLWrapper.glColor(OGLWrapper.colorFill);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + width, y);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x, y + height);
			GL11.glEnd();
		}
		if (OGLWrapper.colorStroke != null) {
			OGLWrapper.glColor(OGLWrapper.colorStroke);
			GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + width, y);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x, y + height);
			GL11.glEnd();
		}
	}

	public static void line(int x, int y, int x2, int y2) {
		if (colorStroke != null) {
			OGLWrapper.glColor(colorStroke);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x2, y2);
			GL11.glEnd();	
		}
	}
	
	public static void line(double x, double y, double x2, double y2) {
		if (colorStroke != null) {
			OGLWrapper.glColor(colorStroke);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2d(x, y);
			GL11.glVertex2d(x2, y2);
			GL11.glEnd();	
		}
	}
	
	
	/**
	 * Loads the shader and sets up the vao's for 2D screen space drawing
	 */
	@SuppressWarnings("static-access")
	public static void setup() {
		
		stack = new MatrixStack();
		
		shaderFilledRectangle = new Shader("shaders/filled_rectangle_vert.glsl", "shaders/filled_rectangle_frag.glsl");
		
		float[] vertices = { 
				1f, 1f, 0.0f,
				1f, 0f, 0.0f,
				0f, 0f, 0.0f,
				0f, 1f, 0.0f,
		};
		
		int[] indices = {
				0, 1, 3,
				1, 2, 3
		};
		
		int[] indicesQuad = {
				0,1,2,3
		};
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(vertices.length);
			fb.put(vertices).flip();
			
			// Setup filled rects
			IntBuffer ib = stack.mallocInt(indices.length);
			ib.put(indices).flip();
			
			glidVAOFill = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOFill);

			int glidVBO = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBO);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
		
			int glidEBO = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, glidEBO);
			GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);

			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			GL33.glEnableVertexAttribArray(0);
			
			// Setup stroked rects
			IntBuffer ibQuad = stack.mallocInt(indicesQuad.length);
			ibQuad.put(indicesQuad).flip();
			
			glidVAOStroke = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOStroke);

			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBO);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
		
			int glidEBOStroke = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, glidEBOStroke);
			GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ibQuad, GL33.GL_STATIC_DRAW);

			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			GL33.glEnableVertexAttribArray(0);
		}		
	}

}
