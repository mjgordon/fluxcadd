package ui;

/**
 * Modes that a ContentView can be set to. Defines default camera positions
 */
public enum ViewType {
	PERSP("Perspective", 0, 0, 0, 0, 0, 0),
	TOP("Top", 0, 0, 0, 0, 0, 0),
	BOTTOM("Bottom", 180, 0, 0, 130, 130, 0),
	FRONT("Front", -90, 0, 0, 130, 130, 0),
	BACK("Back", 90, 180, 0, 130, 130, 0),
	LEFT("Left", -90, 0, 90, 130, 130, 0),
	RIGHT("Right", -90, 0, -90, 130, 130, 0);


	public String name;

	public int rotationX;
	public int rotationY;
	public int rotationZ;

	public double translationX;
	public double translationY;
	public double translationZ;


	private ViewType(String name, int rotX, int rotY, int rotZ, int transX, int transY, int transZ) {
		this.name = name;
		this.rotationX = rotX;
		this.rotationY = rotY;
		this.rotationZ = rotZ;
		this.translationX = transX;
		this.translationY = transY;
		this.translationZ = transZ;
	}


	public ViewType getNext() {
		if (this == PERSP)
			return (TOP);
		else if (this == TOP)
			return (BOTTOM);
		else if (this == BOTTOM)
			return (FRONT);
		else if (this == FRONT)
			return (BACK);
		else if (this == BACK)
			return (LEFT);
		else if (this == LEFT)
			return (RIGHT);
		else
			return (PERSP);
	}
}