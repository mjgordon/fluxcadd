package io;

import event.EventManager;

public class MouseButton extends EventManager<MouseButtonEvent> {
	private boolean stateLeft;
	private boolean stateCenter;
	private boolean stateRight;

	private static MouseButton instance = null;

	protected MouseButton() {
	}
	
	public void mouseButtonEvent(MouseButtonEvent e) {
		if (e.button == 0) {
			stateLeft = (e.type == MouseButtonEvent.Type.PRESSED);
		}
		
		if (e.button == 1) {
			stateRight = (e.type == MouseButtonEvent.Type.PRESSED);
		}
		
		if (e.button == 2) {
			stateCenter = (e.type == MouseButtonEvent.Type.PRESSED);
		}
		
		sendMessage(e);
	}

	public boolean leftPressed() {
		return (stateLeft);
	}

	public boolean centerPressed() {
		return (stateCenter);
	}

	public boolean rightPressed() {
		return (stateRight);
	}
	
	public boolean anyPressed() {
		return(stateLeft || stateCenter || stateRight);
	}

	public static MouseButton instance() {
		if (instance == null) {
			instance = new MouseButton();
		}
		return (instance);
	}
}
