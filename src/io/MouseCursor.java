package io;

import event.EventManager;

public class MouseCursor extends EventManager {
	/**
	 * Current mouse X position
	 */
	private int x = 0;

	/**
	 * Current mouse Y position
	 */
	private int y = 0;

	/**
	 * Mouse X position before the most recent move
	 */
	private int pX = 0;

	/**
	 * Mouse Y position before the most recent move
	 */
	private int pY = 0;

	/**
	 * Singleton instance
	 */
	private static MouseCursor instance = null;


	private MouseCursor() {

	}


	public void mouseCursorEvent(MouseCursorEvent e) {
		pX = x;
		pY = y;
		x = (int) e.x;
		y = (int) e.y;
		sendMessage(e);
	}


	public int getX() {
		return (x);
	}


	public int getY() {
		return (y);
	}


	/**
	 * Get the change in mouse X position from the last position
	 * @return
	 */
	public int getDX() {
		return x - pX;
	}


	/**
	 * Get the change in mouse Y position from the last position
	 * @return
	 */
	public int getDY() {
		return y - pY;
	}


	public static MouseCursor instance() {
		if (instance == null) {
			instance = new MouseCursor();
		}
		return instance;
	}
}
