package io;

import event.EventManager;

public class MouseCursor extends EventManager {
	private int x = 0;
	private int y = 0;
	
	private int pX = 0;
	private int pY = 0;
	
	private static MouseCursor instance = null;
	
	protected MouseCursor() {
		
	}
	
	public void mouseCursorEvent(MouseCursorEvent e) {
		pX = x;
		pY = y;
		x = (int)e.x;
		y = (int)e.y;
		sendMessage(e);
	}
	
	public int getX() {
		return(x);
	}
	
	public int getY() {
		return(y);
	}
	
	public int getDX() {
		return(x - pX);
	}
	
	public int getDY() {
		return(y - pY);
	}
	
	public static MouseCursor instance() {
		if (instance == null) {
			instance = new MouseCursor();
		}
		return (instance);
	}
}
