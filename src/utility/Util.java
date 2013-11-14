package utility;

import static org.lwjgl.opengl.GL11.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public class Util {
	public static float strokeRed = 1;
	public static float strokeGreen = 1;
	public static float strokeBlue = 1;

	public static float fillRed = 0;
	public static float fillGreen = 0;
	public static float fillBlue = 0;

	public static boolean fill = true;
	public static boolean stroke = true;

	public static final float PI = (float) Math.PI;
	public static final float HALF_PI = PI / 2;
	public static final float TWO_PI = PI * 2;

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

	public static final float red(int rgb) {
		return (((rgb >> 16) & 0xff) / 255.f);
	}

	public static final float green(int rgb) {
		return (((rgb >> 8) & 0xff) / 255.f);
	}

	public static final float blue(int rgb) {
		return (((rgb) & 0xff) / 255.f);
	}

	public static void rect(int x, int y, int width, int height) {
		if (fill) {
			glColor3f(fillRed, fillGreen, fillBlue);
			glBegin(GL_QUADS);
			glVertex2i(x, y);
			glVertex2i(x + width, y);
			glVertex2i(x + width, y + height);
			glVertex2i(x, y + height);
			glEnd();
		}
		if (stroke) {
			glColor3f(strokeRed, strokeGreen, strokeBlue);
			glBegin(GL_LINE_LOOP);
			glVertex2i(x, y);
			glVertex2i(x + width, y);
			glVertex2i(x + width, y + height);
			glVertex2i(x, y + height);
			glEnd();
		}
	}
	
	public static void color(float r, float g, float b) {
		glColor3f(r,g,b);
	}

	public static void line(int x, int y, int x2, int y2) {
		glColor3f(strokeRed, strokeGreen, strokeBlue);
		glBegin(GL_LINES);
		glVertex2i(x, y);
		glVertex2i(x2, y2);
		glEnd();
	}

	// TODO Fix this.
	public static void screenshot() {
		glReadBuffer(GL_FRONT);
		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();
		int bpp = 4; // Assuming a 32-bit display with a byte each for red,
						// green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		File file = new File("screenshot.png"); // The file to save to.
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16)
						| (g << 8) | b);
			}
		}

		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// All angles for these next few functions are in radians

	public static PVector sphereToCart(PVector in) {
		return (sphereToCart(in.x, in.y, in.z));
	}

	public static PVector sphereToCart(float r, float i, float a) {
		float x = (float) (r * Math.sin(i) * Math.cos(a));
		float y = (float) (r * Math.sin(i) * Math.sin(a));
		float z = (float) (r * Math.cos(i));
		return (new PVector(x, y, z));
	}

	public static PVector cartToSphere(PVector in) {
		return (cartToSphere(in.x, in.y, in.z));
	}

	public static PVector cartToSphere(float x, float y, float z) {
		float r = (float) Math.sqrt((x * x) + (y * y) + (z * z));
		float i = (float) Math.acos(z / r);
		float a = (float) Math.atan2(y, x);
		return (new PVector(r, i, a));
	}

	// Math Functions stolen from processing to make PVector work right

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

	public static char keyToChar() {
		return (keyToChar(Keyboard.getEventKey()));
	}

	public static char keyToChar(int k) {
		char c = 0;

		if (k == Keyboard.KEY_0) c = '0';
		else if (k == Keyboard.KEY_1) c = '1';
		else if (k == Keyboard.KEY_2) c = '2';
		else if (k == Keyboard.KEY_3) c = '3';
		else if (k == Keyboard.KEY_4) c = '4';
		else if (k == Keyboard.KEY_5) c = '5';
		else if (k == Keyboard.KEY_6) c = '6';
		else if (k == Keyboard.KEY_7) c = '7';
		else if (k == Keyboard.KEY_8) c = '8';
		else if (k == Keyboard.KEY_9) c = '9';

		else if (k == Keyboard.KEY_A) c = 'A';
		else if (k == Keyboard.KEY_B) c = 'B';
		else if (k == Keyboard.KEY_C) c = 'C';
		else if (k == Keyboard.KEY_D) c = 'D';
		else if (k == Keyboard.KEY_E) c = 'E';
		else if (k == Keyboard.KEY_F) c = 'F';
		else if (k == Keyboard.KEY_G) c = 'G';
		else if (k == Keyboard.KEY_H) c = 'H';
		else if (k == Keyboard.KEY_I) c = 'I';
		else if (k == Keyboard.KEY_J) c = 'J';
		else if (k == Keyboard.KEY_K) c = 'K';
		else if (k == Keyboard.KEY_L) c = 'L';
		else if (k == Keyboard.KEY_M) c = 'M';
		else if (k == Keyboard.KEY_N) c = 'N';
		else if (k == Keyboard.KEY_O) c = 'O';
		else if (k == Keyboard.KEY_P) c = 'P';
		else if (k == Keyboard.KEY_Q) c = 'Q';
		else if (k == Keyboard.KEY_R) c = 'R';
		else if (k == Keyboard.KEY_S) c = 'S';
		else if (k == Keyboard.KEY_T) c = 'T';
		else if (k == Keyboard.KEY_U) c = 'U';
		else if (k == Keyboard.KEY_V) c = 'V';
		else if (k == Keyboard.KEY_W) c = 'W';
		else if (k == Keyboard.KEY_X) c = 'X';
		else if (k == Keyboard.KEY_Y) c = 'Y';
		else if (k == Keyboard.KEY_Z) c = 'Z';

		else if (k == Keyboard.KEY_MINUS) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) c = '_';
			else c = '-';
		}
		else if (k == Keyboard.KEY_PERIOD) c = '.';
		else if (k == Keyboard.KEY_SLASH) c = '/';
		else if (k == Keyboard.KEY_UNDERLINE) c = '_';
		else if (k == Keyboard.KEY_SPACE) c = ' ';
		
		return(c);
	}

}
