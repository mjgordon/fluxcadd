package backend;

/**
 * Provides basic startup, render loop, and shutdown functions
 *
 */
public interface Backend {
	public abstract void init();
	public abstract void loop();
	public abstract void stop();
	public abstract int getWidth();
	public abstract int getHeight();
}
