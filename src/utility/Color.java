package utility;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;

public class Color {
	public int a;
	public int r;
	public int g;
	public int b;
	
	public Color(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
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

	
	public static void setGlColor(Color c) {
		float r = c.r / 255.0f;
		float g = c.g / 255.0f;
		float b = c.b / 255.0f;
		
		glColor3f(r, g, b);
	}
	
	public static void setGlColor(Color c, int a) {
		float r = c.r / 255.0f;
		float g = c.g / 255.0f;
		float b = c.b / 255.0f;
		
		glColor4f(r, g, b, a / 255.0f);
	}
}
