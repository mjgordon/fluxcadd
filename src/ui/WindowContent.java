package ui;


public abstract class WindowContent {
	public Window parent;

	public abstract void keyPressed();
	public abstract void render();
	public abstract void mouseWheel(float amt);
	public abstract void mousePressed();
	public abstract void mouseDragged();
	
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
