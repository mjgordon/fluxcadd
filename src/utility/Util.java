package utility;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;

public class Util {
	public static float strokeRed = 1;
	public static float strokeGreen = 1;
	public static float strokeBlue = 1;
	
	public static float fillRed = 0;
	public static float fillGreen = 0;
	public static float fillBlue = 0;
	
	public static boolean fill = true;
	public static boolean stroke  = true;
	
	/**
	 * Sets the fill color, on a 0-1 scale.
	 */
	public static void fill(float r, float g, float b) {
		fill = true;
		fillRed = r;
		fillGreen = g;
		fillBlue = b;
	}
	
	/**
	 * Sets the fill color from a single hex value
	 */
	public static void fill(int rgb) {
		fill = true;
		
		float r = (rgb >> 16) & 0xff;
		float g = (rgb >> 8) & 0xff;
		float b = (rgb) & 0xff;

		fillRed = r / 255.f;
		fillGreen = g / 255.f;
		fillBlue = b / 255.f;
	}
	
	/**
	 * Sets the stroke color, on a 0-1 scale.
	 */
	public static void stroke(float r, float g, float b) {
		stroke = true;
		strokeRed = r;
		strokeGreen = g;
		strokeBlue = b;
	}
	
	/**
	 * Sets the stroke color from a single hex value
	 */
	public static void stroke(int rgb) {
		stroke = true;
		float r = (rgb >> 16) & 0xff;
		float g = (rgb >> 8) & 0xff;
		float b = (rgb) & 0xff;

		strokeRed = r / 255.f;
		strokeGreen = g / 255.f;
		strokeBlue = b / 255.f;
	}
	
	public static void noFill() {
		fill = false;
	}
	
	public static void noStroke() {
		stroke = false;
	}
	
	public static void rect(int x, int y, int width, int height) {
		if (fill) {
			glColor3f(fillRed,fillGreen,fillBlue);
			glBegin(GL_QUADS);
				glVertex2i(x,y);
				glVertex2i(x + width, y);
				glVertex2i(x + width, y + height);
				glVertex2i(x, y + height);
			glEnd();
		}
		if (stroke) {
			glColor3f(strokeRed,strokeGreen,strokeBlue);
			glBegin(GL_LINE_LOOP);
				glVertex2i(x,y);
				glVertex2i(x + width, y);
				glVertex2i(x + width, y + height);
				glVertex2i(x, y + height);
			glEnd();
		}
	}
	
	public static void line(int x, int y, int x2, int y2) {
		glColor3f(strokeRed,strokeGreen,strokeBlue);
		glBegin(GL_LINES);
			glVertex2i(x,y);
			glVertex2i(x2,y2);
		glEnd();
	}
	
	public static void screenshot() {
		glReadBuffer(GL_FRONT);
		int width = Display.getDisplayMode().getWidth();
		int height= Display.getDisplayMode().getHeight();
		int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer );
		
		File file = new File("screenshot.png"); // The file to save to.
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		  
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}
		  
		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	//Math Functions stolen from processing to make PVector work right
	
	static public final float dist(float x1, float y1, float x2, float y2) {
		return (float) Math.sqrt(sq(x2 - x1) + sq(y2 - y1));
	}

	static public final float dist(float x1, float y1, float z1, float x2,
			float y2, float z2) {
		return (float) Math.sqrt(sq(x2 - x1) + sq(y2 - y1) + sq(z2 - z1));
	}

	static public final float sq(float n) {
		return n * n;
	}
	
	static public final float lerp(float start, float stop, float amt) {
		return start + (stop - start) * amt;
	}

}
