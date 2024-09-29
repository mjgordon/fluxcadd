package utility;

import io.Keyboard;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.Random;

import javax.imageio.ImageIO;

import main.FluxCadd;
import utility.math.UtilMath;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;

import intersection.Intersection;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Generic static utility functions and constants, some should probably be split
 * into more specifically named files. Some functions are copied out of the
 * Processing system for color etc.
 *
 */

public class Util {
	public static void screenshot() {
		glReadBuffer(GL_FRONT);
		int width = FluxCadd.backend.getWidth();
		int height = FluxCadd.backend.getHeight();

		int bpp = 4; // Assuming a 32-bit display with a byte each for red,
						// green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

		File file = new File("screenshot.png"); // The file to save to.
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}

		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static Vector3d sphericalToCartesian(Vector3d in) {
		return (sphericalToCartesian(in.x, in.y, in.z));
	}


	public static Vector3d sphericalToCartesian(double radius, double inclination, double azimuth) {
		double x = radius * Math.sin(inclination) * Math.cos(azimuth);
		double y = radius * Math.sin(inclination) * Math.sin(azimuth);
		double z = radius * Math.cos(inclination);

		return (new Vector3d(x, y, z));
	}


	public static Vector3d cartesianToSpherical(Vector3d in) {
		return (cartesianToSpherical(in.x, in.y, in.z));
	}


	public static Vector3d cartesianToSpherical(double x, double y, double z) {
		double r = Math.sqrt((x * x) + (y * y) + (z * z));
		double i = Math.acos(z / r);
		double a = Math.atan2(y, x);
		return (new Vector3d(r, i, a));
	}


	private static Random internalRandom;


	public static final float random(float high) {
		// avoid an infinite loop when 0 or NaN are passed in
		if (high == 0 || high != high) {
			return 0;
		}

		if (internalRandom == null) {
			internalRandom = new Random();
		}

		// for some reason (rounding error?) Math.random() * 3
		// can sometimes return '3' (once in ~30 million tries)
		// so a check was added to avoid the inclusion of 'howbig'
		float value = 0;
		do {
			value = internalRandom.nextFloat() * high;
		} while (value == high);
		return value;
	}


	public static final float random(float low, float high) {
		if (low >= high)
			return low;
		float diff = high - low;
		float value = 0;
		// because of rounding error, can't just add low, otherwise it may hit high
		// https://github.com/processing/processing/issues/4551
		do {
			value = random(diff) + low;
		} while (value == high);
		return value;
	}


	public static final float arraySum(float[] input) {
		float total = 0;

		for (float f : input) {
			total += f;
		}
		return total;
	}


	public static final double arraySum(double[] input) {
		double total = 0;

		for (double d : input) {
			total += d;
		}
		return total;
	}


	public static char keyToChar(int k) {
		char c = 0;

		if (k == GLFW_KEY_0 || k == GLFW_KEY_KP_0)
			c = '0';
		else if (k == GLFW_KEY_1 || k == GLFW_KEY_KP_1)
			c = '1';
		else if (k == GLFW_KEY_2 || k == GLFW_KEY_KP_2)
			c = '2';
		else if (k == GLFW_KEY_3 || k == GLFW_KEY_KP_3)
			c = '3';
		else if (k == GLFW_KEY_4 || k == GLFW_KEY_KP_4)
			c = '4';
		else if (k == GLFW_KEY_5 || k == GLFW_KEY_KP_5)
			c = '5';
		else if (k == GLFW_KEY_6 || k == GLFW_KEY_KP_6)
			c = '6';
		else if (k == GLFW_KEY_7 || k == GLFW_KEY_KP_7)
			c = '7';
		else if (k == GLFW_KEY_8 || k == GLFW_KEY_KP_8)
			c = '8';
		else if (k == GLFW_KEY_9 || k == GLFW_KEY_KP_9)
			c = '9';

		else if (k == GLFW_KEY_A)
			c = 'A';
		else if (k == GLFW_KEY_B)
			c = 'B';
		else if (k == GLFW_KEY_C)
			c = 'C';
		else if (k == GLFW_KEY_D)
			c = 'D';
		else if (k == GLFW_KEY_E)
			c = 'E';
		else if (k == GLFW_KEY_F)
			c = 'F';
		else if (k == GLFW_KEY_G)
			c = 'G';
		else if (k == GLFW_KEY_H)
			c = 'H';
		else if (k == GLFW_KEY_I)
			c = 'I';
		else if (k == GLFW_KEY_J)
			c = 'J';
		else if (k == GLFW_KEY_K)
			c = 'K';
		else if (k == GLFW_KEY_L)
			c = 'L';
		else if (k == GLFW_KEY_M)
			c = 'M';
		else if (k == GLFW_KEY_N)
			c = 'N';
		else if (k == GLFW_KEY_O)
			c = 'O';
		else if (k == GLFW_KEY_P)
			c = 'P';
		else if (k == GLFW_KEY_Q)
			c = 'Q';
		else if (k == GLFW_KEY_R)
			c = 'R';
		else if (k == GLFW_KEY_S)
			c = 'S';
		else if (k == GLFW_KEY_T)
			c = 'T';
		else if (k == GLFW_KEY_U)
			c = 'U';
		else if (k == GLFW_KEY_V)
			c = 'V';
		else if (k == GLFW_KEY_W)
			c = 'W';
		else if (k == GLFW_KEY_X)
			c = 'X';
		else if (k == GLFW_KEY_Y)
			c = 'Y';
		else if (k == GLFW_KEY_Z)
			c = 'Z';

		else if (k == GLFW_KEY_MINUS) {
			if (Keyboard.instance().keyDown(GLFW_KEY_LEFT_SHIFT))
				c = '_';
			else
				c = '-';
		}
		else if (k == GLFW_KEY_PERIOD)
			c = '.';
		else if (k == GLFW_KEY_SLASH)
			c = '/';
		else if (k == GLFW_KEY_SPACE)
			c = ' ';

		return (c);
	}


	public static double absoluteAngleDifference(double a, double b) {
		// Normalize in here just to make sure?
		if (a > b) {
			if (a - b < Math.PI) {
				return (a - b);
			}

			else {
				return (UtilMath.TWO_PI - (a - b));
			}

		}
		else {
			if (b - a < Math.PI) {
				return (b - a);
			}

			else {
				return (UtilMath.TWO_PI - (a - b));
			}

		}
	}


	/**
	 * Converts the X and Y values of a PVector to an array of bytes
	 * 
	 * @param vector
	 * @return
	 */
	public static byte[] vector2DToByteArray(Vector3d vector) {
		ByteBuffer b = ByteBuffer.allocate(8);
		b.putInt(0, (int) (vector.x * 100));
		b.putInt(4, (int) (vector.y * 100));

		return (b.array());
	}


	public static boolean arrayContainsChar(final char[] array, final char v) {
		for (final char e : array)
			if (e == v)
				return true;
		return false;
	}


	/**
	 * Gets around a quirk of the JScheme integration. Apparently can't do implicit
	 * widening and unboxing at the same time during object construction from within
	 * JScheme.
	 * 
	 */
	public static float explicitFloat(double f) {
		return ((float) f);
	}


	public static float explicitFloat(float f) {
		return (f);
	}


	public static float explicitFloat(int i) {
		return ((float) i);
	}


	/**
	 * From :
	 * https://stackoverflow.com/questions/21114796/3d-ray-quad-intersection-test-in-java
	 * Not well integrated as it just takes raw vectors as input for now
	 */
	public static Intersection intersectRayWithSquare(Vector3d R1, Vector3d R2, Vector3d S1, Vector3d S2, Vector3d S3) {
		// System.out.println("d1 : " + PVector.dist(S1, S2));
		// System.out.println("d2 : " + PVector.dist(S1, S3));
		// 1.
		Vector3d dS21 = new Vector3d(S2).sub(S1);
		Vector3d dS31 = new Vector3d(S3).sub(S1);
		Vector3d n = dS21.cross(dS31);

		// 2.
		Vector3d dR = new Vector3d(R1).sub(R2);

		double ndotdR = n.dot(dR);

		if (Math.abs(ndotdR) < 1e-6f) { // Choose your tolerance
			return null;
		}

		double t = -n.dot(new Vector3d(R1).sub(S1)) / ndotdR;
		if (t > 0) {
			return (null);
		}

		Vector3d M = new Vector3d(dR).mul(t).add(R1);

		// 3.
		Vector3d dMS1 = new Vector3d(M).sub(S1);
		double u = dMS1.dot(dS21);
		double v = dMS1.dot(dS31);

		double maxU = dS21.dot(dS21);
		double maxV = dS31.dot(dS31);

		// 4.
		if (u >= 0.0f && u <= maxU && v >= 0.0f && v <= maxV) {
			return (new Intersection(M, new Vector2d(u / maxU, v / maxV)));
		}
		else {
			return (null);
		}

	}


	public static String getTimestamp() {
		String out = "";

		LocalDateTime time = LocalDateTime.now();
		out += time.getYear() + "_";
		out += time.getMonthValue() + "_";
		out += time.getDayOfMonth();

		out += "-";

		out += time.getHour() + "_";
		out += time.getMinute() + "_";
		out += time.getSecond();

		return (out);
	}

}
