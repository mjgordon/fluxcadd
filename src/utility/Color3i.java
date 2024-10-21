package utility;

import utility.math.UtilMath;

public class Color3i {
	public int r;
	public int g;
	public int b;
	
	public Color3i(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public Color3i(float r, float g, float b) {
		this.r = (int)r;
		this.g = (int)g;
		this.b = (int)b;
	}
	
	public Color3i(double r, double g, double b) {
		this.r = (int)r;
		this.g = (int)g;
		this.b = (int)b;
	}
	
	public Color3i(int color) {
		int mask = 0xFF;
		this.r = (color >> 16) & mask;
		this.g = (color >> 8) & mask;
		this.b = (color >> 0) & mask;
	}
	
	public Color3i(String hex) {
		if (hex.substring(0,2).equals("0x") == false) {
			hex = "0x" + hex;
		}
		
		long rgbRaw = Long.decode(hex);
		int rgb = (int) rgbRaw;
		
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = (rgb) & 0xff;

		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public Color3i copy() {
		return new Color3i(r, g, b);
	}
	
	public static Color3i lerpColor(Color3i a, Color3i b, double factor) {
		return (new Color3i(UtilMath.lerp(a.r, b.r, factor), UtilMath.lerp(a.g, b.g, factor), UtilMath.lerp(a.b, b.b, factor)));
	}
	
	public int toInt() {
		int out = b;
		out += (g << 8);
		out += (r << 16);

		return (out);
	}
	
	public void mult(double factor) {
		this.r = UtilMath.clip((int) (r * factor), 0, 255);
		this.g = UtilMath.clip((int) (g * factor), 0, 255);
		this.b = UtilMath.clip((int) (b * factor), 0, 255);
	}
}
