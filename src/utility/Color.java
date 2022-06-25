/**
 * Based on the Processing color implementation
 * Assumes range 0-255 for ARGB channels
 */

package utility;

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
		this.r = (int)r;
		this.g = (int)g;
		this.b = (int)b;
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
	
	public Color(PVectorD v) {
		this.r = Util.clip((int)v.x,0,255);
		this.g = Util.clip((int)v.y,0,255);
		this.b = Util.clip((int)v.z,0,255);
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
		if (colorModeA == 255)
			return outgoing;
		return (outgoing / 255.0f) * colorModeA;
		*/
		return outgoing;
	}


	public static final float red(int rgb) {
		float c = (rgb >> 16) & 0xff;
		/*
		if (colorModeDefault)
			return c;
		return (c / 255.0f) * colorModeX;
		*/
		return c;
	}


	public static final float green(int rgb) {
		float c = (rgb >> 8) & 0xff;
		/*
		if (colorModeDefault)
			return c;
		return (c / 255.0f) * colorModeY;
		*/
		return c;
	}


	public static final float blue(int rgb) {
		float c = (rgb) & 0xff;
		/*
		if (colorModeDefault)
			return c;
		return (c / 255.0f) * colorModeZ;
		*/
		return c;
	}


	@Override
	public String toString() {
		return (r + "," + g + "," + b + " : " + a);
	}
	
	public PVectorD getVector() {
		return new PVectorD(r,g,b);
	}
}
