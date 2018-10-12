package io;

import event.EventManager;

public class MouseWheel extends EventManager {
	private static MouseWheel instance = null;
	
	protected MouseWheel() {
		
	}
	
	public void mouseWheelEvent(MouseWheelEvent e) {
		sendMessage(e);
	}
	
	public static MouseWheel instance() {
		if (instance == null) {
			instance = new MouseWheel();
		}
		return(instance);
	}
}
