package controller;

public abstract class Controller {
	
	public String name;
	
	public boolean selected = false;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	public ControllerManager parent;
	
	String displayName;
	
	public Controller(ControllerManager parent, String name, int x,int y,int width,int height) {
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		
		this.parent = parent;
	}
	
	public boolean pick(int x, int y) {
		return(x > this.x && x < this.x + width && y > this.y && y < this.y + height);
	}
	
	public void keyPressed() {}
	
	
	public abstract void render();
	
	public abstract void execute();
}
