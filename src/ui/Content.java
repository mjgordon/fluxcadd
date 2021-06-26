package ui;



/**
 * Content subclasses are the actual content of a Panel sub-window. 
 * May include 3d views, GUI interfaces, terminals etc.
 */

public abstract class Content {
	private Panel parent;
	

	public abstract void render();
	protected abstract void keyPressed(int key);
	protected abstract void textInput(char character);
	protected abstract void mouseWheel(float amt);
	protected abstract void mousePressed(int button, int mouseX, int mouseY);
	protected abstract void mouseDragged(int dx, int dy);
	
	public Content(Panel parent) {
		this.parent = parent;
	}
	
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
	
	public void setParentWindowTitle(String title) {
		parent.windowTitle = title;
	}
}
