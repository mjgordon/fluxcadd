package controller;

/**
 * 
 * @author mgordon
 *
 */
public interface Controllable {
	public abstract void controllerEvent(String name);
	public abstract int getX();
	public abstract int getY();
	public abstract int getWidth();
	public abstract int getHeight();
}
