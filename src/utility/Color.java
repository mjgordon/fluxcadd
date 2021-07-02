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
}
