package controller;

/**
 * Any object that has a ControllerManager and has to react to Controllers
 *
 */
public interface Controllable {
	/**
	 * Called whenever one of the associated controllers has an event. Only passes the name, the
	 * @param name
	 */
	public abstract void controllerEvent(UserInterfaceElement<? extends UserInterfaceElement<?>> controller);
	public abstract int getX();
	public abstract int getY();
	public abstract int getWidth();
	public abstract int getHeight();
}
