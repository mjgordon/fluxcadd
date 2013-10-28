package geometry;

public abstract class Geometry {
	
	float r = 1;
	float g = 1;
	float b = 1;
	
	public abstract void render();
	
	public void color(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
}
