package io;

import event.EventMessage;

public class MouseButtonEvent extends EventMessage {
	public final int mouseX;
	public final int mouseY;
	public final int button;
	public final Type type;
	
	
	public enum Type {
		PRESSED,
		RELEASED
	}
	
	
	public MouseButtonEvent(int mouseX, int mouseY, int button,Type action) {
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.button = button;
		this.type = action;
	}
	
	
	@Override
	public String toString() {
		return "MouseButtonEvent : " + button + " " + type;
	}
	
}
