package graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

public class Primitives {
	
	protected static Shader shaderFilledRectangle;
	
	protected static int glidVAO;
	
	
	public static void rect2(Matrix4f projection, int x, int y, int width, int height) {
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
		shaderFilledRectangle.setVec3("color", 1.0f, 0.0f, 1.0f);
		shaderFilledRectangle.setMatrix4("projection", projection);

		GL33.glBindVertexArray(glidVAO);
		//GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL); // Normal
		GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0);
		
		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL33.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL33.glPopMatrix();
	}
	
	public static void rect(int x, int y, int width, int height) {
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
		if (OGLWrapper.colorStroke != null) {
			OGLWrapper.glColor(OGLWrapper.colorStroke);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x2, y2);
			GL11.glEnd();	
		}
	}
	
	public static void line(double x, double y, double x2, double y2) {
		if (OGLWrapper.colorStroke != null) {
			OGLWrapper.glColor(OGLWrapper.colorStroke);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2d(x, y);
			GL11.glVertex2d(x2, y2);
			GL11.glEnd();	
		}
	}
	
	@SuppressWarnings("static-access")
	public static void setupShader() {
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
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(vertices.length);
			fb.put(vertices).flip();
			
			IntBuffer ib = stack.mallocInt(indices.length);
			ib.put(indices).flip();
			
			glidVAO = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAO);

			int glidVBO = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBO);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
		
			int glidEBO = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, glidEBO);
			GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);

			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			GL33.glEnableVertexAttribArray(0);
		}		
	}

}
