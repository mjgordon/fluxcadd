/**
 * Based on the Processing color implementation
 * Assumes range 0-255 for ARGB channels
 */

package utility;

import org.joml.Vector3d;

import utility.math.UtilMath;

public class Color {
	public int a = 255;
	public int r = 255;
	public int g = 255;
	public int b = 255;


	public Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = 255;
	}


	public Color(float r, float g, float b) {
		this.r = (int) r;
		this.g = (int) g;
		this.b = (int) b;
		this.a = 255;
	}


	public Color(double r, double g, double b) {
		this.r = (int) r;
		this.g = (int) g;
		this.b = (int) b;
		this.a = 255;
	}


	public Color(int rgb) {
		int a = (rgb >> 24) & 0xff;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = (rgb) & 0xff;

		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	public Color(String hex) {
		if (hex.substring(0,2).equals("0x") == false) {
			hex = "0x" + hex;
		}
		
		long rgbRaw = Long.decode(hex);
		int rgb = (int) rgbRaw;
		
		int a = (rgb >> 24) & 0xff;
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = (rgb) & 0xff;

		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		
	}

	public Color(Vector3d v) {
		this.r = UtilMath.clip((int) v.x, 0, 255);
		this.g = UtilMath.clip((int) v.y, 0, 255);
		this.b = UtilMath.clip((int) v.z, 0, 255);
	}


	public int toInt() {
		int out = b;
		out += (g << 8);
		out += (r << 16);
		out += (a << 24);

		return (out);
	}


	@Override
	public String toString() {
		return (r + "," + g + "," + b + " : " + a);
	}


	public Vector3d getVector() {
		return new Vector3d(r, g, b);
	}


	public static Color lerpColor(Color a, Color b, double factor) {
		return (new Color(UtilMath.lerp(a.r, b.r, factor), UtilMath.lerp(a.g, b.g, factor), UtilMath.lerp(a.b, b.b, factor)));
	}


	public Color copy() {
		return (new Color(r, g, b));
	}


	public void set(Color c) {
		this.a = c.a;
		this.r = c.r;
		this.g = c.g;
		this.b = c.b;
	}


	public void mult(double factor) {
		this.r = UtilMath.clip((int) (r * factor), 0, 255);
		this.g = UtilMath.clip((int) (g * factor), 0, 255);
		this.b = UtilMath.clip((int) (b * factor), 0, 255);
	}


	public static final int blue(int rgb) {
		return (rgb) & 0xff;
	}


	public static final int green(int rgb) {
		return (rgb >> 8) & 0xff;
	}


	public static final int red(int rgb) {
		return (rgb >> 16) & 0xff;
	}

}
