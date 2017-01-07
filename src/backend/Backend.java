package backend;

public abstract class Backend {
	public abstract void init();
	public abstract void loop();
	public abstract void stop();
	public abstract int getWidth();
	public abstract int getHeight();
}
