package graphics;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;
import org.lwjgl.system.MemoryStack;

import utility.Color3i;

import static graphics.Graphics.*;

/**
 * For drawing screen-pixel space elements (e.g. GUI)
 * Maintains a matrix stack similar to fixed-pipeline OpenGL
 */
public class Graphics2D {
	
	private static Shader shader;
	
	private static Shader shaderText;
	
	private static int glidVAOFillRect;
	private static int glidVAOStrokeRect;
	private static int glidVAOStrokeLine;
	private static int glidVAOText;
	private static int glidVBOCharacters;
	private static int glidTextureFontBlack;
	private static int glidTextureFontWhite;
	
	/**
	 * Maximum number of characters drawn in one line
	 */
	private static final int textSize = 256;
	
	
	public static Color3i colorFill = null;
	public static Color3i colorStroke = null;
	
	
	public static final int textCellWidth = 8;
	public static final int textCellHeight = 12;
	
	
	public static Matrix4f matrixProjection;
	
	private static Matrix4f matrixViewProjection;
	
	private static MatrixStack stack;
	
	
	
	
	
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
	
	
	public static void sendMatrixViewProjection() {
		matrixViewProjection = new Matrix4f(matrixProjection).mul(stack.get());
		shader.use();
		shader.setMatrix4("transformation", matrixViewProjection);
		
		shaderText.use();
		shaderText.setMatrix4("transformation", matrixViewProjection);
	}
	
	
	public static void translate(int x, int y) {
		stack.get().translate(x, y, 0);
		sendMatrixViewProjection();
	}
	
	
	public static void pushStack() {
		stack.push();
	}
	
	
	public static void popStack() {
		stack.pop();
		sendMatrixViewProjection();
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
		Matrix3f shape = new Matrix3f();
		shape.m00(width);
		shape.m11(height);
		shape.m20(x);
		shape.m21(y);
		shape.m22(1);
		
		shader.use();
		shader.setVec3("color", color.r / 255.0f, color.g / 255.0f, color.b / 255.0f);
		shader.setMatrix3("shape", shape);

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
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL); // Normal	
	}
	
	
	@SuppressWarnings("static-access")
	public static void line(double x, double y, double x2, double y2) {
		if (colorStroke == null) {
			return;
		}
		
		float diffX = (float)(x2 - x);
		float diffY = (float)(y2 - y);
		Matrix3f shape = new Matrix3f();
		shape.m00(diffX);
		shape.m01(diffY);
		shape.m10(-diffY);
		shape.m11(diffX);
		shape.m20((float)x);
		shape.m21((float)y);
		
		shader.use();
		
		shader.setVec3("color", colorStroke.r / 255.0f, colorStroke.g / 255.0f, colorStroke.b / 255.0f);
		shader.setMatrix3("shape", shape);
		
		GL33.glBindVertexArray(glidVAOStrokeLine);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_LINE); // Wireframe
		GL33.glDrawArrays(GL33.GL_LINES, 0, 2);
			
		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL); // Normal	
	}
	
	
	@SuppressWarnings("static-access")
	public static void text(int x, int y, String text, boolean black) {
		if (text.length() == 0) {
			return;
		}
	
		shaderText.use();
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			
			float[] update = new float[textSize];
			for (int i = 0; i < update.length; i++) {
				update[i] = 0;
			}
			for (int i = 0; i < text.length(); i++) {
				update[i] = (float)text.charAt(i);
			}
			
			FloatBuffer fbUpdate = stack.mallocFloat(update.length);
			fbUpdate.put(update).flip();

			GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBOCharacters);
			GL33.glBufferSubData(GL33.GL_ARRAY_BUFFER, 0, fbUpdate);
		}
		
		shaderText.setVec2("start", x, y);
		shaderText.setVec2("cellOffset", textCellWidth, textCellHeight);  // TODO: Move this to setup
		
		GL33.glEnable(GL33.GL_TEXTURE_2D);
		
		if (black) {
			GL33.glBindTexture(GL33.GL_TEXTURE_2D,  glidTextureFontBlack);
		}
		else {
			GL33.glBindTexture(GL33.GL_TEXTURE_2D,  glidTextureFontWhite);	
		}
		GL33.glBindVertexArray(glidVAOText);
		
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL);
		GL33.glDrawElementsInstanced(GL33.GL_TRIANGLES, 6, GL33.GL_UNSIGNED_INT, 0, text.length());
			
		GL33.glBindTexture(GL33.GL_TEXTURE_2D,  0);
		GL33.glBindVertexArray(0);
		GL33.glUseProgram(0);
		GL33.glPolygonMode(GL33.GL_FRONT_AND_BACK, GL33.GL_FILL);
		GL33.glDisable(GL33.GL_TEXTURE_2D);
	}
	
	
	/**
	 * Loads the shader and sets up the vao's for 2D screen space drawing
	 * Should be called during program initialization
	 */
	@SuppressWarnings("static-access")
	public static void setup() {
		
		stack = new MatrixStack();
		
		shader = new Shader("shaders/geom_2d_vp_vert.glsl", "shaders/uniform_color_frag.glsl");
		
		shaderText = new Shader("shaders/text_bitmap_vp_vert.glsl", "shaders/textured_frag.glsl");
		
		try (MemoryStack stack = MemoryStack.stackPush()) {
			// Setup Rects
			{
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
				
				// Setup filled rects
				glidVAOFillRect = initVAO();
				int glidVBO = initVBO(stack, verticesRect);
				initEBO(stack, indicesRectFill);
	
				GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 8, 0);
				GL33.glEnableVertexAttribArray(0);
				
				// Setup stroked rects
				glidVAOStrokeRect = initVAO();
				GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBO);
	
				GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 8, 0);
				GL33.glEnableVertexAttribArray(0);
			}
			
			// Setup line
			{
				float[] verticesLine = {
						0f, 0f,
						1f, 0f
				};
				
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
			

			// Setup text
			{
				float sixteenth = 1 / 16.0f;
				float[] verticesText = { 
						textCellWidth, textCellHeight, sixteenth, sixteenth, 
						textCellWidth, 0f,  sixteenth, 0, 
						0f, 0f,  0, 0, 
						0f, textCellHeight, 0, sixteenth,
				};
				
				int[] indicesRectFill = {
						0, 1, 3,
						1, 2, 3
				};
				
				float[] characters = new float[textSize];
				for (int i = 0; i < textSize; i++) {
					characters[i] = (float)(i);
				}
				
				glidVBOCharacters = initVBODynamic(stack, characters);
				
				ImageLoader image = new ImageLoader("data/font.png");
				
				ByteBuffer imageBuffer = image.buffer;
				ByteBuffer imageBufferBlack = imageToBufferBW(imageBuffer, new Color3i(0, 0, 0));
				glidTextureFontBlack = GL33.glGenTextures();
				GL33.glBindTexture(GL33.GL_TEXTURE_2D, glidTextureFontBlack);
				GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, image.width, image.height, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, imageBufferBlack);
				GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);
				GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_REPEAT);	
				GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_REPEAT);
				GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR_MIPMAP_LINEAR);
				GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
				
				ByteBuffer imageBufferWhite = imageToBufferBW(imageBuffer, new Color3i(255, 255, 255));
				glidTextureFontWhite = GL33.glGenTextures();
				GL33.glBindTexture(GL33.GL_TEXTURE_2D, glidTextureFontWhite);
				GL33.glTexImage2D(GL33.GL_TEXTURE_2D, 0, GL33.GL_RGBA, image.width, image.height, 0, GL33.GL_RGBA, GL33.GL_UNSIGNED_BYTE, imageBufferWhite);
				GL33.glGenerateMipmap(GL33.GL_TEXTURE_2D);
				GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_S, GL33.GL_REPEAT);	
				GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_WRAP_T, GL33.GL_REPEAT);
				GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MIN_FILTER, GL33.GL_LINEAR_MIPMAP_LINEAR);
				GL33.glTexParameteri(GL33.GL_TEXTURE_2D, GL33.GL_TEXTURE_MAG_FILTER, GL33.GL_LINEAR);
				
				glidVAOText = initVAO();
				initVBO(stack, verticesText);
				initEBO(stack, indicesRectFill);
				
				GL33.glEnableVertexAttribArray(0);
				GL33.glVertexAttribPointer(0, 2, GL33.GL_FLOAT, false, 16, 0);
				
				GL33.glEnableVertexAttribArray(1);
				GL33.glVertexAttribPointer(1, 2, GL33.GL_FLOAT, false, 16, 8);
				
				GL33.glEnableVertexAttribArray(2);
				GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, glidVBOCharacters);
				GL33.glVertexAttribPointer(2, 1, GL33.GL_FLOAT, false, 4, 0);
				GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
				GL33.glVertexAttribDivisor(2, 1);
				
				GL33.glBindBuffer(GL33.GL_ARRAY_BUFFER, 0);
				GL33.glBindTexture(GL33.GL_TEXTURE_2D, 0);
			}
		}		
	}
	
	
	/**
	 * Loads a black and white image as a font, with white pixels being filled and black pixels being transparent
	 * @param image
	 * @return
	 */
	private static ByteBuffer imageToBufferBW(ByteBuffer image, Color3i color) {
	    int size = image.capacity();

	    ByteBuffer buffer = ByteBuffer.allocateDirect(size);
	    
	    for (int i = 0; i < size; i += 4) {
	    	boolean filled = (image.get(i) & 0xFF) > 0;
	    	if (filled) {
	    		buffer.put((byte) color.r);
	    		buffer.put((byte) color.g);
	    		buffer.put((byte) color.b);
	    		buffer.put((byte) 255);
	    	}
	    	else {
	    		buffer.put((byte) 0);
	    		buffer.put((byte) 0);
	    		buffer.put((byte) 0);
	    		buffer.put((byte) 0);
	    	}
	    }
	    buffer.flip();
	    return buffer;
	}

}
