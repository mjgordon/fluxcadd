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
	
	protected static Shader shader;
	
	protected static int glidVAOFillRect;
	protected static int glidVAOStrokeRect;
	protected static int glidVAOStrokeLine;
	
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
		if (colorFill != null) {
			rectInternal(x, y, width, height, colorFill, true);
		}
		if (colorStroke != null) {
			rectInternal(x, y, width, height, colorStroke, false);
		}
	}
	
	
	@SuppressWarnings("static-access")
	private static void rectInternal(int x, int y, int width, int height, Color3i color, boolean filled) {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL33.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL33.glPushMatrix();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		
		Matrix4f shape = new Matrix4f();
		shape.m00(width);
		shape.m11(height);
		shape.setTranslation(x, y, 0);
		
		shader.use();
		shader.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shader.setMatrix4("projection", stack.get());
		shader.setMatrix4("shape", shape);

		if (filled) {
			GL33.glBindVertexArray(glidVAOFillRect);
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL); // Normal	
			GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0);
		}
		else {
			GL33.glBindVertexArray(glidVAOStrokeRect);
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
			GL33.glDrawArrays(GL33.GL_LINE_LOOP, 0, 4);
		}
			
		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL33.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL33.glPopMatrix();
		
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL); // Normal	
	}
	
	
	@SuppressWarnings("static-access")
	public static void line(double x, double y, double x2, double y2) {
		if (colorStroke == null) {
			return;
		}
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL33.glPushMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL33.glPushMatrix();
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		
		// This seems silly
		float diffX = (float)(x2 - x);
		float diffY = (float)(y2 - y);
		Matrix4f shape = new Matrix4f();
		shape.m00(diffX);
		shape.m01(diffY);
		shape.m10(-diffY);
		shape.m11(diffX);
		shape.setTranslation((float)x, (float) y, 0);
		
		shader.use();
		
		shader.setVec3("color", colorStroke.r / 255.0f, colorStroke.g / 255.0f, colorStroke.b / 255.0f);
		shader.setMatrix4("shape", shape);
		shader.setMatrix4("projection", stack.get());
		
		GL33.glBindVertexArray(glidVAOStrokeLine);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINES, 0, 2);
			
		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL33.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL33.glPopMatrix();
		
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL); // Normal	
	}
	
	
	/**
	 * Loads the shader and sets up the vao's for 2D screen space drawing
	 * Should be called during program initialization
	 */
	@SuppressWarnings("static-access")
	public static void setup() {
		
		stack = new MatrixStack();
		
		shader = new Shader("shaders/geom_2d_vert.glsl", "shaders/uniform_color_frag.glsl");
		
		float[] verticesRect = { 
				1f, 1f,
				1f, 0f,
				0f, 0f,
				0f, 1f
		};
		
		int[] indicesRectFill = {
				0, 1, 3,
				1, 2, 3
		};
		
		float[] verticesLine = {
				0.0f, 0.0f,
				1.0f, 0.0f
		};
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(verticesRect.length);
			fb.put(verticesRect).flip();
			
			// Setup filled rects
			IntBuffer ib = stack.mallocInt(indicesRectFill.length);
			ib.put(indicesRectFill).flip();
			
			glidVAOFillRect = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOFillRect);

			int glidVBO = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBO);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
		
			int glidEBO = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, glidEBO);
			GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);

			GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 8, 0);
			GL33.glEnableVertexAttribArray(0);
			
			// Setup stroked rects
			glidVAOStrokeRect = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOStrokeRect);

			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBO);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);

			GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 8, 0);
			GL33.glEnableVertexAttribArray(0);
			
			// Setup line
			FloatBuffer fbLine = stack.mallocFloat(verticesLine.length);
			fbLine.put(verticesLine).flip();
			
			glidVAOStrokeLine = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOStrokeLine);

			int glidVBOLine = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBOLine);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fbLine, GL33.GL_STATIC_DRAW);

			GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 8, 0);
			GL33.glEnableVertexAttribArray(0);
			
			GL33.glBindVertexArray(0);
		}		
	}

}
