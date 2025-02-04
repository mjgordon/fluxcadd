package controller;

import java.util.function.Consumer;

import graphics.OGLWrapper;
import graphics.Primitives;

public abstract class UserInterfaceElement<T extends UserInterfaceElement<T>> {

	protected String name;
	public String displayName;

	protected int x;
	protected int y;
	protected int width;
	protected int height;

	/**
	 * Under normal conditions, true when the UIE has been clicked, then false after the mouse is released (anywhere)
	 */
	protected boolean selected = false;

	protected int displayX;
	protected int displayY;

	public static boolean debugOutlines = false;
	protected int debugOutlineColor = 0x00FFFF;

	protected boolean fullWidth = false;

	private Consumer<T> execCallback;
	
	
	public boolean visible = true;


	public UserInterfaceElement(String name, String displayName, int x, int y, int width, int height) {
		this.name = name;
		this.displayName = displayName;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		this.displayX = 0;
		this.displayY = height + 5;
	}


	public UserInterfaceElement<? extends UserInterfaceElement<?>> pick(int mouseX, int mouseY) {
		if (mouseX > this.x && mouseX < this.x + width && mouseY > this.y && mouseY < this.y + height) {
			selected = true;
			return this;
		}
		else {
			selected = false;
			return null;
		}
	}


	@SuppressWarnings("unchecked")
	public T setCallback(Consumer<T> c) {
		this.execCallback = c;
		return (T) this;
	}


	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}


	public int getX() {
		return this.x;
	}


	public int getY() {
		return this.y;
	}


	public int getWidth() {
		return this.width;
	}


	public int getHeight() {
		return this.height;
	}


	public void setWidth(int width) {
		this.width = width;
	}


	public void setHeight(int height) {
		this.height = height;
	}


	public int getLayoutWidth() {
		return Math.max(this.width, displayName.length() * 8 + displayX);
	}


	public int getLayoutHeight() {
		return Math.max(this.height, displayY + 12);
	}


	public String getName() {
		return name;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	protected void keyPressed(int key) {
	}

	protected void textInput(char character) {
	}


	protected void mouseDragged(int x, int y, int dx, int dy) {
	}


	public void mouseReleased() {
		selected = false;
	}


	public void mouseWheel(int delta) {
	}


	protected void render() {
		if (debugOutlines && visible) {
			OGLWrapper.stroke(debugOutlineColor);
			OGLWrapper.noFill();
			Primitives.rect(x, y, getLayoutWidth(), getLayoutHeight());
		}
	}


	@SuppressWarnings("unchecked")
	public void execute() {
		if (execCallback != null) {
			execCallback.accept((T) this);
		}
	}


	/**
	 * Overwrite if a UIE needs to do something during a reflow (such as a stack
	 * resetting its children)
	 */
	public void reflow() {
	}
}
