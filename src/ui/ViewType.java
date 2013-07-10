package ui;

import utility.PVector;

public enum ViewType {
	PERSP("Perspective",-45,0,0),
	TOP("Top",0,0,0),
	BOTTOM("Bottom",180,0,0),
	FRONT("Front",-90,0,0),
	BACK("Back",90,180,0),
	LEFT("Left",-90,0,90),
	RIGHT("Right",-90,0,-90);
	
	public String name;
	
	public PVector defaultTranslation;
    public int rotationX;
	public int rotationY;
	public int rotationZ;
	
	private ViewType(String name,int rotX,int rotY,int rotZ) {
		this.name = name;
		this.rotationX = rotX;
		this.rotationY = rotY;
		this.rotationZ = rotZ;
	}
	
	public ViewType getNext() {
		if (this == PERSP) return(TOP);
		else if (this == TOP) return(BOTTOM);
		else if (this == BOTTOM) return(FRONT);
		else if (this == FRONT) return(BACK);
		else if (this == BACK) return(LEFT);
		else if (this == LEFT) return(RIGHT);
		else return(PERSP);
	}
}
