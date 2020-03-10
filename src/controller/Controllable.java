package controller;

/**
 * Any object that has a ControllerManager and has to react to Controllers
 *
 */
public interface Controllable {
	
	public abstract void controllerEvent(String name);
	public abstract int getX();
	public abstract int getY();
	public abstract int getWidth();
	public abstract int getHeight();
}
