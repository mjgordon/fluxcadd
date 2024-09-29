package ui;

import event.EventManager;

/**
 * Content subclasses are the actual content of a Panel sub-window. 
 * May include 3d views, GUI interfaces, terminals etc.
 */

public abstract class Content extends EventManager {
	protected Panel parent;
	
	public abstract void render();
	protected abstract void keyPressed(int key);
	protected abstract void textInput(char character);
	protected abstract void mouseWheel(float amt);
	protected abstract void mousePressed(int button, int mouseX, int mouseY);
	protected abstract void mouseReleased(int button);
	protected abstract void mouseDragged(int button, int dx, int dy);
	
	public abstract void resizeRespond(int newWidth, int newHeight);
	
	public Content(Panel parent) {
		this.parent = parent;
	}
	
	public int getX() {
		return parent.getX();
	}
	
	public int getY() {
		return parent.getY();
	}
	
	public int getWidth() {
		return parent.getWidth();
	}
	
	public int getHeight() {
		return parent.getHeight();
	}
	
	public void setParentWindowTitle(String title) {
		parent.windowTitle = title;
	}
	
	public void setParent(Panel parent) {
		this.parent = parent;
	}
	
}
