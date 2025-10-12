package graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;


/**
 * Helper functions for working with OpenGL objects
 */
public class Graphics {
	/**
	 * Generates an id for a VAO and binds it
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static int initVAO() {
		int glidVAO = GL33.glGenVertexArrays();
		GL33.glBindVertexArray(glidVAO);	
		return glidVAO;
	}
	
	
	/**
	 * Generates an id for a VBO, fills it from a float array, and binds it
	 * @param stack - Must be called within a 'try (MemoryStack stack = MemoryStack.stackPush())' block
	 * @param vertices
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static int initVBO(MemoryStack stack, float[] vertices) {
		FloatBuffer fb = stack.mallocFloat(vertices.length);
		fb.put(vertices).flip();
		int glidVBO = GL33.glGenBuffers();
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBO);
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_STATIC_DRAW);
		return glidVBO;
	}
	
	
	@SuppressWarnings("static-access")
	public static int initVBODynamic(MemoryStack stack, float[] vertices) {
		FloatBuffer fb = stack.mallocFloat(vertices.length);
		fb.put(vertices).flip();
		int glidVBO = GL33.glGenBuffers();
		GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBO);
		GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fb, GL33.GL_DYNAMIC_DRAW);
		return glidVBO;
	}
	
	
	/**
	 * Generates an id for an EBO, fills it from an int array, and binds it
	 * @param stack - Must be called within a 'try (MemoryStack stack = MemoryStack.stackPush())' block
	 * @param indices
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static int initEBO(MemoryStack stack, int[] indices) {
		IntBuffer ib = stack.mallocInt(indices.length);
		ib.put(indices).flip();
		int glidEBO = GL33.glGenBuffers();
		GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, glidEBO);
		GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ib, GL33.GL_STATIC_DRAW);
		return glidEBO;
	}
}
