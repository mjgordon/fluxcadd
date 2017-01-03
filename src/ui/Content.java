package ui;


public abstract class Content {
	public Panel parent;

	public abstract void render();
	public abstract void keyPressed(int key);
	public abstract void mouseWheel(float amt);
	public abstract void mousePressed(int button, int mouseX, int mouseY);
	public abstract void mouseDragged(int dx, int dy);
	
	public int getX() {
		return(parent.getX());
	}
	
	public int getY() {
		return(parent.getY());
	}
	
	public int getWidth() {
		return(parent.getWidth());
	}
	
	public int getHeight() {
		return(parent.getHeight());
	}
}
