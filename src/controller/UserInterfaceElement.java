package controller;

import graphics.OGLWrapper;
import graphics.Primitives;

public abstract class UserInterfaceElement {
	
	protected Controllable target;
	
	protected String name;
	public String displayName;
	
	
	
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	protected boolean selected = false;
	
	protected int displayX;
	protected int displayY;
	
	public static boolean debugOutlines;
	protected int debugOutlineColor = 0x00FFFF; 
	
	
	public UserInterfaceElement(Controllable target, String name, String displayName,int x,int y,int width,int height) {
		this.target = target;
		
		this.name = name;
		this.displayName = displayName;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
			
		this.displayX = 0;
		this.displayY = height + 5;
	}
	
	
	public UserInterfaceElement pick(int x, int y) {
		if (x > this.x && x < this.x + width && y > this.y && y < this.y + height) {
			return(this);
		}
		else {
			return(null);
		}
	}
	
	
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	public int getX() {
		return(this.x);
	}
	
	
	public int getY() {
		return(this.y);
	}
	
	
	public int getWidth() {
		return this.width;
	}
	
	
	public int getHeight() {
		return this.height;
	}
	
	
	public int getLayoutWidth() {
		return (Math.max(this.width, displayName.length() * 8 + displayX));
	}
	
	
	public int getLayoutHeight() {
		return(Math.max(this.height, displayY + 12));
	}
	
	
	public String getName() {
		return name;
	}
	
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	public abstract void keyPressed(int key);
	
	
	public abstract void textInput(char character);
	
	
	public void render() {
		if (debugOutlines) {
			OGLWrapper.stroke(debugOutlineColor);
			OGLWrapper.noFill();
			Primitives.rect(x, y, getLayoutWidth(), getLayoutHeight());	
		}
	}
	
	public void execute() {
		target.controllerEvent(this);
	}
}
