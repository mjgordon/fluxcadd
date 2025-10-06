package graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import utility.Color3i;

public class Graphics3D {
	private static Shader shaderUniformColor;
	
	private static Shader shaderVertexColors;
	
	public static Shader shaderTextured;
	
	public static Shader shaderPoint;
	
	public static Shader shaderPointCloud;

	public static Matrix4f projection;

	public static Matrix4f view;

	private static int glidVAOAxes;
	
	private static int glidVAOBox;
	
	private static int glidVAOCross;
	
	private static int glidVAOEllipse;
	
	private static int glidVAOGrid;
	
	private static int glidVAOLine;
	
	private static int glidVAOPoint;
	
	private static int glidVAORectOutline;
	
	private static int glidVAORectTextured;


	public static void setView(Matrix4f _view) {
		view = new Matrix4f(_view);
	}


	public static void setProjection(Matrix4f _proj) {
		projection = new Matrix4f(_proj);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawAxes(Matrix4d modelMatrix) {
		shaderVertexColors.use();
		shaderVertexColors.setMatrix4("model", modelMatrix);
		shaderVertexColors.setMatrix4("view", view);
		shaderVertexColors.setMatrix4("projection", projection);
		
		GL33.glBindVertexArray(glidVAOAxes);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINES ,0,6);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawCross(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shaderUniformColor.setMatrix4("model", modelMatrix);
		shaderUniformColor.setMatrix4("view", view);
		shaderUniformColor.setMatrix4("projection", projection);
		
		GL33.glBindVertexArray(glidVAOCross);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);
		GL33.glDrawArrays(GL33.GL_LINES,0,6);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
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
	
	
	@SuppressWarnings("static-access")
	public static void drawGrid(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shaderUniformColor.setMatrix4("model", modelMatrix);
		shaderUniformColor.setMatrix4("view", view);
		shaderUniformColor.setMatrix4("projection", projection);
		
		GL33.glBindVertexArray(glidVAOGrid);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINES,0, 84);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawEllipse(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shaderUniformColor.setMatrix4("model", modelMatrix);
		shaderUniformColor.setMatrix4("view", view);
		shaderUniformColor.setMatrix4("projection", projection);

		GL33.glBindVertexArray(glidVAOEllipse);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINE_LOOP,0, 32);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawLine(Vector3d start, Vector3d end, Color3i color) {
		// TODO: Clean this up to reduce allocation
		
		Vector3d diffD = new Vector3d(end).sub(start);
		Vector3f diff = new Vector3f((float)diffD.x, (float)diffD.y, (float)diffD.z);
		
		Vector3f diff2 = (new Vector3f()).orthogonalize(diff);
		Vector3f diff3 = diff.cross(diff2, new Vector3f());
		
		
		Matrix4f shape = new Matrix4f();
		shape.m00(diff.x);
		shape.m01(diff.y);
		shape.m02(diff.z);
		
		shape.m10(diff2.x);
		shape.m11(diff2.y);
		shape.m12(diff2.z);
		
		shape.m20(diff3.x);
		shape.m21(diff3.y);
		shape.m22(diff3.z);
		
		shape.m30((float)start.x);
		shape.m31((float)start.y);
		shape.m32((float)start.z);
		
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shaderUniformColor.setMatrix4("model", shape);
		shaderUniformColor.setMatrix4("view", view);
		shaderUniformColor.setMatrix4("projection", projection);

		GL33.glBindVertexArray(glidVAOLine);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINES,0,2);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawPoint(Vector3d position, Color3i color) {
		GL33.glEnable(GL33.GL_PROGRAM_POINT_SIZE);
		shaderPoint.use();
		shaderPoint.setInt("pointSize", 5);
		shaderPoint.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shaderPoint.setVec4("translation", (float)position.x, (float)position.y, (float)position.z, false);
		shaderPoint.setMatrix4("view", view);
		shaderPoint.setMatrix4("projection", projection);
		
		GL33.glBindVertexArray(glidVAOPoint);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL);
		GL33.glDrawArrays(GL33.GL_POINTS,0,1);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawPointCloud(int glidVAOPointCloud, int length, Matrix4d modelMatrix) {
		shaderPointCloud.use();
		shaderPointCloud.setMatrix4("model", modelMatrix);
		shaderPointCloud.setMatrix4("view", view);
		shaderPointCloud.setMatrix4("projection", projection);
		
		GL33.glBindVertexArray(glidVAOPointCloud);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL);
		GL33.glDrawArrays(GL33.GL_POINTS,0,length);

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
	public static void drawPolyLine(int glidVAOPolyline, int length, Matrix4d modelMatrix, Color3i color) {
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
	
	
	@SuppressWarnings("static-access")
	public static void drawRect(Matrix4d modelMatrix, Color3i color, int textureId) {
		
		if (textureId == -1) {
			shaderUniformColor.use();
			shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
			shaderUniformColor.setMatrix4("model", modelMatrix);
			shaderUniformColor.setMatrix4("view", view);
			shaderUniformColor.setMatrix4("projection", projection);
			
			GL33.glBindVertexArray(glidVAORectOutline);
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);
			GL33.glDrawArrays(GL33.GL_LINE_LOOP, 0, 4);
			
		}
		else {
			GL33.glEnable(GL33.GL_TEXTURE_2D);
			shaderTextured.use();	
			shaderTextured.setMatrix4("model", modelMatrix);
			shaderTextured.setMatrix4("view", view);
			shaderTextured.setMatrix4("projection", projection);
			
			GL33.glBindTexture(GL33.GL_TEXTURE_2D,  textureId);
			
			GL33.glBindVertexArray(glidVAORectTextured);
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL);
			
			GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0);
			
			GL33.glDisable(GL33.GL_TEXTURE_2D);
		}
		
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
		
		shaderVertexColors = new Shader("shaders/geom_3d_vertex_colors_vert.glsl", "shaders/uniform_color_frag.glsl");
		
		shaderTextured = new Shader("shaders/geom_3d_textured_vert.glsl", "shaders/textured_frag.glsl");
		
		shaderPoint = new Shader("shaders/geom_3d_point_vert.glsl", "shaders/point_frag.glsl");
		
		shaderPointCloud = new Shader("shaders/geom_3d_point_cloud_vert.glsl", "shaders/point_frag.glsl");
		
		float[] verticesAxes = {
				0, 0, 0, 1, 0, 0,
				1, 0, 0, 1, 0, 0,
				0, 0, 0, 0, 1, 0,
				0, 1, 0, 0, 1, 0,
				0, 0, 0, 0, 0, 1, 
				0, 0, 1, 0, 0, 1
		};
		
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
		
		float[] verticesCross = {
				-1, 0, 0,
				1, 0, 0,
				0, -1, 0,
				0, 1, 0, 
				0, 0, -1, 
				0, 0, 1, 
		};
		
		float[] verticesEllipse = new float[96];
		for (int i = 0; i < 32; i++) {
			double n = i / 32.0 * Math.PI * 2;
			verticesEllipse[i * 3 + 0] = (float)Math.cos(n);
			verticesEllipse[i * 3 + 1] = (float)Math.sin(n);
			verticesEllipse[i * 3 + 2] = 0;
		}
		
		float[] verticesGrid = new float[168];
		for (int i = 0; i < 21; i++) {
			verticesGrid[i * 4 + 0] = -10;
			verticesGrid[i * 4 + 1] = i - 10;
			verticesGrid[i * 4 + 2] = 10;
			verticesGrid[i * 4 + 3] = i - 10;
			
			verticesGrid[i * 4 + 0 + 84] = i - 10;
			verticesGrid[i * 4 + 1 + 84] = -10;
			verticesGrid[i * 4 + 2 + 84] = i - 10;
			verticesGrid[i * 4 + 3 + 84] = 10;
		}
		
		float[] verticesLine = {
				0,0,0,
				1,0,0
		};
		
		float[] verticesPoint = {
				0, 0, 0
		};
		
		float[] verticesRect = { 
				0.5f, 0.5f, 0.0f,    1.0f, 1.0f,
				0.5f, -0.5f, 0.0f,   1.0f, 0.0f, 
				-0.5f, -0.5f, 0.0f,  0.0f, 0.0f, 
				-0.5f, 0.5f, 0.0f,   0.0f, 1.0f
		};
		
		int[] indicesRectTextured = {
				0, 1, 3,
				1, 2, 3
		};
		
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Setup Axes
			FloatBuffer fbAxes = stack.mallocFloat(verticesAxes.length);
			fbAxes.put(verticesAxes).flip();
			
			glidVAOAxes = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOAxes);
			
			int glidVBOAxes = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBOAxes);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fbAxes, GL33.GL_STATIC_DRAW);
			
			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 24, 0);
			
			GL33.glEnableVertexAttribArray(1);
			GL33.glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 24, 12);
			
			// Setup cross
			FloatBuffer fbCross = stack.mallocFloat(verticesCross.length);
			fbCross.put(verticesCross).flip();
			
			glidVAOCross = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOCross);
			
			int glidVBOCross = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBOCross);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fbCross, GL33.GL_STATIC_DRAW);
			
			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			
			// Setup Box
			FloatBuffer fbBox = stack.mallocFloat(verticesBox.length);
			fbBox.put(verticesBox).flip();
			
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
			
			// Setup Ellipse
			FloatBuffer fbEllipse = stack.mallocFloat(verticesEllipse.length);
			fbEllipse.put(verticesEllipse).flip();
			
			glidVAOEllipse = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOEllipse);
			
			int glidVBOEllipse = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBOEllipse);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fbEllipse, GL33.GL_STATIC_DRAW);
			
			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			
			// Setup Grid
			FloatBuffer fbGrid = stack.mallocFloat(verticesGrid.length);
			fbGrid.put(verticesGrid).flip();
			glidVAOGrid = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOGrid);
			
			int glidVBOGrid = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBOGrid);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fbGrid, GL33.GL_STATIC_DRAW);
			
			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 8, 0);
			
			// Setup Line
			FloatBuffer fbLine = stack.mallocFloat(verticesLine.length);
			fbLine.put(verticesLine).flip();
			
			glidVAOLine = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOLine);
			
			int glidVBOLine = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBOLine);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fbLine, GL33.GL_STATIC_DRAW);
			
			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			
			// Setup Point
			FloatBuffer fbPoint = stack.mallocFloat(verticesPoint.length);
			fbPoint.put(verticesPoint).flip();
			glidVAOPoint = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAOPoint);
			
			int glidVBOPoint = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBOPoint);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fbPoint, GL33.GL_STATIC_DRAW);
			
			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			
			// Setup Rect
			FloatBuffer fbRect = stack.mallocFloat(verticesRect.length);
			fbRect.put(verticesRect).flip();
			glidVAORectTextured = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAORectTextured);
			
			int glidVBORect = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBORect);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fbRect, GL33.GL_STATIC_DRAW);
			
			IntBuffer ibRectTextured = stack.mallocInt(indicesRectTextured.length);
			ibRectTextured.put(indicesRectTextured).flip();
			
			int glidEBORectTextured = GL33.glGenBuffers();
			GL33.glBindBuffer(GL33.GL_ELEMENT_ARRAY_BUFFER, glidEBORectTextured);
			GL33.glBufferData(GL33.GL_ELEMENT_ARRAY_BUFFER, ibRectTextured, GL33.GL_STATIC_DRAW);
			
			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 20, 0);
			
			GL33.glEnableVertexAttribArray(1);
			GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 20, 12);
			
			glidVAORectOutline = GL33.glGenVertexArrays();
			GL33.glBindVertexArray(glidVAORectOutline);
			
			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBORect);
			GL33.glBufferData(GL33.GL_ARRAY_BUFFER, fbRect, GL33.GL_STATIC_DRAW);
			
			GL33.glEnableVertexAttribArray(0);
			GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 20, 0);
		}
	}
}
