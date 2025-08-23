package graphics;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;

import utility.Color3i;

public class Graphics3D {
	private static Shader shaderUniformColor;
	
	private static Matrix4f projection;
	
	private static Matrix4f view;
	

	public static void setView(Matrix4f _view) {
		view = new Matrix4f(_view);
	}
	
	public static void setProjection(Matrix4f _proj) {
		projection = new Matrix4f(_proj);
	}
	
	
	/**
	 * Draw a polyline, using a unique VAO
	 * @param glidVAOPolyline
	 * @param length
	 * @param matrix
	 * @param color
	 */
	@SuppressWarnings("static-access")
	public static void polyline(int glidVAOPolyline, int length, Matrix4d matrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shaderUniformColor.setMatrix4("model", matrix);
		shaderUniformColor.setMatrix4("view", view);
		shaderUniformColor.setMatrix4("projection", projection);

		GL33.glBindVertexArray(glidVAOPolyline);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINE_STRIP, 0, length);
			
		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	/**
	 * Loader shaders and set up matrixes for 3D primitives
	 */
	public static void setup() {
		shaderUniformColor = new Shader("shaders/geom_3d_vert.glsl", "shaders/uniform_color_frag.glsl");
		
		projection = new Matrix4f();
		view = new Matrix4f();
	}
	
	
	
}
