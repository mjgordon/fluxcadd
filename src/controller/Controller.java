package controller;

public abstract class Controller {
	
	protected String name;
	
	protected boolean selected = false;
	
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	protected ControllerManager parent;
	
	public String displayName;
	
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
	
	public abstract void keyPressed(int key);
	
	public abstract void textInput(char character);
	
	public abstract void render();
	
	public abstract void execute();
}
