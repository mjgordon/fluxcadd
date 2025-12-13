package graphics;

import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import static graphics.Graphics.*;

import utility.Color3i;

public class Graphics3D {
	private static Shader shaderUniformColor;
	
	private static Shader shaderVertexColors;
	
	public static Shader shaderTextured;
	
	public static Shader shaderPoint;
	
	public static Shader shaderPointCloud;

	public static Matrix4f matrixViewProjection;
	
	public static Matrix4f matrixMVP;

	private static int glidVAOAxes;
	
	private static int glidVAOBox;
	
	private static int glidVAOCross;
	
	private static int glidVAOCylinder;
	
	private static int glidVAODiamond;
	
	private static int glidVAOEllipse;
	
	private static int glidVAOGrid;
	
	private static int glidVAOLine;
	
	private static int glidVAOPoint;
	
	private static int glidVAORectOutline;
	
	private static int glidVAORectTextured;
	
	private static int glidVAOSphere;
	
	
	public static void setMatrices(Matrix4f view, Matrix4f projection) {
		matrixViewProjection = (new Matrix4f(projection)).mul(view);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawAxes(Matrix4d modelMatrix) {
		shaderVertexColors.use();
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderVertexColors.setMatrix4("transformation", matrixMVP);
		
		GL33.glBindVertexArray(glidVAOAxes);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINES ,0,6);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawBox(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderUniformColor.setMatrix4("transformation", matrixMVP);

		GL33.glBindVertexArray(glidVAOBox);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);
		GL33.glDrawElements(GL33.GL_LINES, 24, GL33.GL_UNSIGNED_INT, 0);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawCross(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderUniformColor.setMatrix4("transformation", matrixMVP);
		
		GL33.glBindVertexArray(glidVAOCross);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);
		GL33.glDrawArrays(GL33.GL_LINES,0,6);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawCylinder(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderUniformColor.setMatrix4("transformation", matrixMVP);
		
		GL33.glBindVertexArray(glidVAOCylinder);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);
		GL33.glDrawElements(GL33.GL_LINES, 96, GL33.GL_UNSIGNED_INT, 0);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawDiamond(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderUniformColor.setMatrix4("transformation", matrixMVP);
		
		GL33.glBindVertexArray(glidVAODiamond);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);
		GL33.glDrawElements(GL33.GL_LINES, 24, GL33.GL_UNSIGNED_INT, 0);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawEllipse(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderUniformColor.setMatrix4("transformation", matrixMVP);

		GL33.glBindVertexArray(glidVAOEllipse);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINE_LOOP,0, 32);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
		
	@SuppressWarnings("static-access")
	public static void drawGrid(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderUniformColor.setMatrix4("transformation", matrixMVP);
		
		GL33.glBindVertexArray(glidVAOGrid);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINES,0, 84);

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
		matrixMVP.set(matrixViewProjection).mul(shape);
		shaderUniformColor.setMatrix4("transformation", matrixMVP);

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
		matrixMVP.set(matrixViewProjection);
		shaderPoint.setMatrix4("transformation", matrixMVP);
		
		GL33.glBindVertexArray(glidVAOPoint);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL);
		GL33.glDrawArrays(GL33.GL_POINTS,0,1);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawPointCloud(int glidVAOPointCloud, int length, Matrix4d modelMatrix) {
		shaderPointCloud.use();
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderPointCloud.setMatrix4("transformation", matrixMVP);
		
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
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderUniformColor.setMatrix4("transformation", matrixMVP);

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
			matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
			shaderUniformColor.setMatrix4("transformation", matrixMVP);
			
			GL33.glBindVertexArray(glidVAORectOutline);
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);
			GL33.glDrawArrays(GL33.GL_LINE_LOOP, 0, 4);
			
		}
		else {
			GL33.glEnable(GL33.GL_TEXTURE_2D);
			shaderTextured.use();	
			matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
			shaderTextured.setMatrix4("transformation", matrixMVP);
			
			GL33.glBindTexture(GL33.GL_TEXTURE_2D,  textureId);
			
			GL33.glBindVertexArray(glidVAORectTextured);
			GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL);
			
			GL33.glDrawElements(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0);
			
			GL33.glDisable(GL33.GL_TEXTURE_2D);
		}
		
		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	@SuppressWarnings("static-access")
	public static void drawSphere(Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderUniformColor.setMatrix4("transformation", matrixMVP);
		
		GL33.glBindVertexArray(glidVAOSphere);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);
		GL33.glDrawElements(GL33.GL_LINES, 480, GL33.GL_UNSIGNED_INT, 0);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}
	
	
	@SuppressWarnings("static-access")
	public static void drawTorus(int glidVAOTorus, Matrix4d modelMatrix, Color3i color) {
		shaderUniformColor.use();
		shaderUniformColor.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		matrixMVP.set(matrixViewProjection).mul(new Matrix4f(modelMatrix));
		shaderUniformColor.setMatrix4("transformation", matrixMVP);
		
		GL33.glBindVertexArray(glidVAOTorus);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE);
		GL33.glDrawElements(GL33.GL_LINES, 512, GL33.GL_UNSIGNED_INT, 0);

		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
	}


	/**
	 * Load shaders and set up matrixes and buffers for 3D primitives
	 */
	@SuppressWarnings("static-access")
	public static void setup() {
		
		matrixViewProjection = new Matrix4f();
		matrixMVP = new Matrix4f();
		
		shaderUniformColor = new Shader("shaders/geom_3d_base_mvp_vert.glsl", "shaders/uniform_color_frag.glsl");
		
		shaderVertexColors = new Shader("shaders/geom_3d_vertex_colors_mvp_vert.glsl", "shaders/uniform_color_frag.glsl");
		
		shaderTextured = new Shader("shaders/geom_3d_textured_mvp_vert.glsl", "shaders/textured_frag.glsl");
		
		shaderPoint = new Shader("shaders/geom_3d_point_mvp_vert.glsl", "shaders/point_frag.glsl");
		
		shaderPointCloud = new Shader("shaders/geom_3d_point_cloud_mvp_vert.glsl", "shaders/point_frag.glsl");
	
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Setup Axes
			{
				float[] verticesAxes = {
						0, 0, 0, 1, 0, 0,
						1, 0, 0, 1, 0, 0,
						0, 0, 0, 0, 1, 0,
						0, 1, 0, 0, 1, 0,
						0, 0, 0, 0, 0, 1, 
						0, 0, 1, 0, 0, 1
				};
				
				glidVAOAxes = initVAO();
				initVBO(stack, verticesAxes);
				
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 24, 0);
				
				GL33.glEnableVertexAttribArray(1);
				GL33.glVertexAttribPointer(1, 3, GL33.GL_FLOAT, false, 24, 12);
			}
			
			// Setup Box
			{
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
				
				glidVAOBox = initVAO();
				initVBO(stack, verticesBox);
				initEBO(stack, indicesBoxStroke);
	
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			}
			
			// Setup cross
			{
				float[] verticesCross = {
						-1, 0, 0,
						1, 0, 0,
						0, -1, 0,
						0, 1, 0, 
						0, 0, -1, 
						0, 0, 1, 
				};
				
				glidVAOCross = initVAO();		
				initVBO(stack, verticesCross);
				
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			}
			
			// Setup Cylinder
			{
				float[] verticesCylinder = new float[96];
				for (int i = 0; i < 16; i++) {
					float n = (float)(i / 16.0 * Math.PI * 2);
					verticesCylinder[i * 3 + 0] = (float)Math.cos(n);
					verticesCylinder[i * 3 + 1] = (float)Math.sin(n);
					verticesCylinder[i * 3 + 2] = 1;
					verticesCylinder[i * 3 + 0 + 48] = (float)Math.cos(n);
					verticesCylinder[i * 3 + 1 + 48] = (float)Math.sin(n);
					verticesCylinder[i * 3 + 2 + 48] = -1;
				}
				
				int[] indicesCylinder = new int[96];
				for (int i = 0; i < 16; i++) {
					indicesCylinder[i * 2] = i;
					indicesCylinder[i * 2 + 1] = i + 16;
					
					indicesCylinder[i * 2 + 32] = i;
					indicesCylinder[i * 2 + 32 + 1] = (i + 1) % 16;
					
					indicesCylinder[i * 2 + 64] = i + 16;
					indicesCylinder[i * 2 + 64 + 1] = (i + 1) % 16 + 16;
				}
				
				glidVAOCylinder = initVAO();
				initVBO(stack, verticesCylinder);
				initEBO(stack, indicesCylinder);
	
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			}
			
			// Setup Diamond
			{
				float[] verticesDiamond = {
						-1, 0, 0,
						1, 0, 0,
						0, -1, 0,
						0, 1, 0, 
						0, 0, -1, 
						0, 0, 1, 
				};
				
				int[] indicesDiamond = {
					0, 2,
					2, 1, 
					1, 3, 
					3, 0,
					4, 0,
					4, 1,
					4, 2, 
					4, 3,
					5, 0,
					5, 1,
					5, 2,
					5, 3
				};
				
				glidVAODiamond = initVAO();
				initVBO(stack, verticesDiamond);
				initEBO(stack, indicesDiamond);
	
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
				
			}
			
			// Setup Ellipse
			{
				float[] verticesEllipse = new float[96];
				for (int i = 0; i < 32; i++) {
					double n = i / 32.0 * Math.PI * 2;
					verticesEllipse[i * 3 + 0] = (float)Math.cos(n);
					verticesEllipse[i * 3 + 1] = (float)Math.sin(n);
					verticesEllipse[i * 3 + 2] = 0;
				}
				
				glidVAOEllipse = initVAO();
				initVBO(stack, verticesEllipse);
				
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			}
			
			// Setup Grid
			{
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
				
				glidVAOGrid = initVAO();
				initVBO(stack, verticesGrid);
				
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 8, 0);
			}
			
			// Setup Line
			{
				float[] verticesLine = {
						0,0,0,
						1,0,0
				};
				
				glidVAOLine = initVAO();
				initVBO(stack,verticesLine);
				
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			}
			
			// Setup Point
			{
				float[] verticesPoint = {
						0, 0, 0
				};
				
				glidVAOPoint = initVAO();
				initVBO(stack, verticesPoint);
				
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			}
			
			// Setup Rect
			{
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
				
				glidVAORectTextured = initVAO();
				int glidVBORect = initVBO(stack, verticesRect);
				initEBO(stack, indicesRectTextured);
				
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 20, 0);
				
				GL33.glEnableVertexAttribArray(1);
				GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 20, 12);
				
				glidVAORectOutline = initVAO();
				GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBORect);
				
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 20, 0);
			}
			
			// Setup Sphere
			{
				float[] verticesSphere = new float[114 * 3];
				int counter = 0;
				for (int v = 1; v < 8; v++) {
					float vX = (float)(v / 8.0f * Math.PI);
					for (int u = 0; u < 16; u++) {
						float uX = (float)(u / 16.0 * Math.PI * 2);
						
						Vector3d cartesian = utility.Util.sphericalToCartesian(1, vX, uX);
						
						verticesSphere[counter * 3 + 0] = (float)cartesian.x;
						verticesSphere[counter * 3 + 1] = (float)cartesian.y;
						verticesSphere[counter * 3 + 2] = (float)cartesian.z;
						counter += 1;
					}
				}
				verticesSphere[112 * 3 + 0] = 0;
				verticesSphere[112 * 3 + 1] = 0;
				verticesSphere[112 * 3 + 2] = 1;
				verticesSphere[113 * 3 + 0] = 0;
				verticesSphere[113 * 3 + 1] = 0;
				verticesSphere[113 * 3 + 2] = -1;
				
				int[] indicesSphere = new int[480];
				int indexCounter = 0;
				// Rings
				for (int v = 1; v < 8; v++) {		
					for (int u = 0; u < 16; u++) {
						indicesSphere[indexCounter * 2 + 0] = indexCounter;
						indicesSphere[indexCounter * 2 + 1] = indexCounter + 1;
						if (u == 15) {
							indicesSphere[indexCounter * 2 + 1] -= 16;
						}
						indexCounter += 1;
					}
				}
				// Sections
				for (int u = 0; u < 16; u++) {
					for (int v = 0; v < 8; v++) {		
						indicesSphere[indexCounter * 2 + 0] = (v-1) * 16 + u;
						indicesSphere[indexCounter * 2 + 1] = v * 16 + u;
						
						if (v == 0) {
							indicesSphere[indexCounter * 2 + 0] = 112;
						}
						else if (v == 7) {
							indicesSphere[indexCounter * 2 + 1] = 113;
						}
						indexCounter += 1;
					}
				}	
				
				glidVAOSphere = initVAO();
				initVBO(stack, verticesSphere);
				initEBO(stack, indicesSphere);
	
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 3, GL33.GL_FLOAT, false, 12, 0);
			}
		}
	}
}
