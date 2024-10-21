package graphics;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import utility.Color3i;

/**
 * Direct interface for calling common opengl functions with internal vector,
 * color etc classes.
 *
 */
public class OGLWrapper {

	public static Color3i colorFill = null;
	public static Color3i colorStroke = null;


	public static void glVertex(Vector3d v) {
		GL11.glVertex3d(v.x, v.y, v.z);
	}


	public static void glNormal(Vector3d v) {
		GL11.glNormal3d(v.x, v.y, v.z);
	}


	public static void glColor(Color3i c) {
		double r = c.r / 255.0;
		double g = c.g / 255.0;
		double b = c.b / 255.0;

		GL11.glColor3d(r, g, b);
	}


	public static void glColor(Color3i c, int a) {
		double r = c.r / 255.0;
		double g = c.g / 255.0;
		double b = c.b / 255.0;

		GL11.glColor4d(r, g, b, a / 255.0f);
	}


	public static void glColor(double r, double g, double b) {
		GL11.glColor3d(r, g, b);
	}
	
	
	public static void glLineWidth(float width) {
		GL11.glLineWidth(width);
	}


	/**
	 * Sets the fill color, on a 0-1 scale.
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
	 * Sets the stroke color, on a 0-1 scale.
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

}
