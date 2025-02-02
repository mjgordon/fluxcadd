package io;

import event.EventManager;

public class MouseButton extends EventManager {
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int CENTER = 2;
	
	private boolean stateLeft;
	private boolean stateCenter;
	private boolean stateRight;

	private static MouseButton instance = null;

	
	private MouseButton() {
	}
	
	
	public void mouseButtonEvent(MouseButtonEvent e) {
		if (e.button == LEFT) {
			stateLeft = e.type == MouseButtonEvent.Type.PRESSED;
		}
		
		if (e.button == RIGHT) {
			stateRight = e.type == MouseButtonEvent.Type.PRESSED;
		}
		
		if (e.button == CENTER) {
			stateCenter = e.type == MouseButtonEvent.Type.PRESSED;
		}
		
		sendMessage(e);
	}

	
	public boolean leftPressed() {
		return stateLeft;
	}

	
	public boolean centerPressed() {
		return stateCenter;
	}

	
	public boolean rightPressed() {
		return stateRight;
	}
	
	
	public boolean anyPressed() {
		return stateLeft || stateCenter || stateRight;
	}
	
	
	/**
	 * Returns the pressed button with the highest priority, -1 if none
	 * @return
	 */
	public int getPressed() {
		int out = -1;
		if (stateCenter) {
			out = CENTER;
		}
		else if (stateRight) {
			out = RIGHT;
		}
		else if (stateLeft) {
			out = LEFT;
		}
		
		return out;
	}

	
	public static MouseButton instance() {
		if (instance == null) {
			instance = new MouseButton();
		}
		return instance;
	}
}
