package io;

import event.EventManager;

import static org.lwjgl.glfw.GLFW.*;

public class Keyboard extends EventManager {
	
	private static Keyboard instance = null;
	
	protected Keyboard() {
		super();
	}
	
	private boolean[] keys = new boolean[65536];
	
	public void keyboardEvent(KeyboardEvent e) {
		if (e.key == -1) {
			return;
		}
		keys[e.key] = (e.type != GLFW_RELEASE );
		sendMessage(e);
	}
	
	public boolean keyDown(int keyCode) {
		return(keys[keyCode]);
	}
	
	public static Keyboard instance() {
		if (instance == null) {
			instance = new Keyboard();
		}
		return(instance);
	}
}
