package ui;


public abstract class WindowContent {
	public Window parent;

	public abstract void keyPressed();
	public abstract void render();
	public abstract void mouseWheel(float amt);
	public abstract void mousePressed();
	public abstract void mouseDragged();
}
