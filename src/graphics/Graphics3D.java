package graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import utility.Color3i;

public class Graphics3D {
	private static Shader shaderUniformColor;

	private static Matrix4f projection;

	private static Matrix4f view;

	private static int glidVAOBox;


	public static void setView(Matrix4f _view) {
		view = new Matrix4f(_view);
	}


	public static void setProjection(Matrix4f _proj) {
		projection = new Matrix4f(_proj);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawBox(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shaderUniformColor.setMatrix4("model", modelMatrix);
		shaderUniformColor.setMatrix4("view", view);
		shaderUniformColor.setMatrix4("projection", projection);

		GL33.glBindVertexArray(glidVAOBox);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawElements(GL33.GL_LINES ,24, GL33.GL_UNSIGNED_INT, 0);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}


	/**
	 * Draw a polyline, using a unique VAO
	 * 
	 * @param glidVAOPolyline
	 * @param length
	 * @param modelMatrix
	 * @param color
	 */
	@SuppressWarnings("static-access")
	public static void polyline(int glidVAOPolyline, int length, Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shaderUniformColor.setMatrix4("model", modelMatrix);
		shaderUniformColor.setMatrix4("view", view);
		shaderUniformColor.setMatrix4("projection", projection);

		GL33.glBindVertexArray(glidVAOPolyline);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINE_STRIP, 0, length);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}


	/**
	 * Load shaders and set up matrixes and buffers for 3D primitives
	 */
	@SuppressWarnings("static-access")
	public static void setup() {
		
		projection = new Matrix4f();
		view = new Matrix4f();
		
		shaderUniformColor = new Shader("shaders/geom_3d_vert.glsl", "shaders/uniform_color_frag.glsl");
		
		/*
		 * @formatter:off
		 *
		 * Order of vertices
		 * 
		 *    7------6 
		 *    |\     |\ 
		 *    | 4------5  +z
		 *    | |    | | 
		 *    | |    | |
		 * +x 3-|----2 | 
		 *     \|     \| 
		 *   -x 0------1  -z
		 *     
		 *     -y      +y
		 * @formatter: on
		 */
		float[] verticesBox = { 
				-1, -1, -1,
				-1,  1, -1, 
				 1,  1, -1,
				 1, -1, -1, 
				 
				-1, -1,  1,
				-1,  1,  1,
				 1,  1,  1,
				 1, -1,  1
		};
		
		
		int[] indicesBoxStroke = {
				0, 1,
				1, 2, 
				2, 3,
				3, 0, 
				4, 5, 
				5, 6, 
				6, 7,
				7, 4, 
				0, 4, 
				1, 5, 
				2, 6, 
				3, 7
		};
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fbBox = stack.mallocFloat(verticesBox.length);
			fbBox.put(verticesBox).flip();
			
			// Setup filled rects
			IntBuffer ibBoxStroke = stack.mallocInt(indicesBoxStroke.length);
			ibBoxStroke.put(indicesBoxStroke).flip();
			
			glidVAOBox = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOBox);

			int glidVBO = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBO);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fbBox, GL33.GL_STATIC_DRAW);
		
			int glidEBO = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, glidEBO);
			GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ibBoxStroke, GL33.GL_STATIC_DRAW);

			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
		}
	}
}
