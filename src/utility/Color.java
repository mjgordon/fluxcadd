/**
 * Based on the Processing color implementation
 * Assumes range 0-255 for ARGB channels
 */

package utility;

import org.joml.Vector3d;

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


	public Color(Vector3d v) {
		this.r = Util.clip((int) v.x, 0, 255);
		this.g = Util.clip((int) v.y, 0, 255);
		this.b = Util.clip((int) v.z, 0, 255);
	}


	public int toInt() {
		int out = b;
		out += (g << 8);
		out += (r << 16);
		out += (a << 24);

		return (out);
	}


	public static final float alpha(int rgb) {
		float outgoing = (rgb >> 24) & 0xff;
		/*
		 * if (colorModeA == 255) return outgoing; return (outgoing / 255.0f) *
		 * colorModeA;
		 */
		return outgoing;
	}


	public static final float red(int rgb) {
		float c = (rgb >> 16) & 0xff;
		/*
		 * if (colorModeDefault) return c; return (c / 255.0f) * colorModeX;
		 */
		return c;
	}


	public static final float green(int rgb) {
		float c = (rgb >> 8) & 0xff;
		/*
		 * if (colorModeDefault) return c; return (c / 255.0f) * colorModeY;
		 */
		return c;
	}


	public static final float blue(int rgb) {
		float c = (rgb) & 0xff;
		/*
		 * if (colorModeDefault) return c; return (c / 255.0f) * colorModeZ;
		 */
		return c;
	}


	@Override
	public String toString() {
		return (r + "," + g + "," + b + " : " + a);
	}


	public Vector3d getVector() {
		return new Vector3d(r, g, b);
	}


	public static Color lerpColor(Color a, Color b, double factor) {
		return (new Color(Util.lerp(a.r, b.r, factor), Util.lerp(a.g, b.g, factor), Util.lerp(a.b, b.b, factor)));
	}
	
	public Color copy() {
		return(new Color(r,g,b));
	}
	
	public void set(Color c) {
		this.a = c.a;
		this.r = c.r;
		this.g = c.g;
		this.b = c.b;
	}
	
	public void mult(double factor) {
		this.r = Util.clip((int) (r * factor),0,255);
		this.g = Util.clip((int) (g * factor),0,255);
		this.b = Util.clip((int) (b * factor),0,255);
	}
	
	
}
