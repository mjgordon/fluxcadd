package io;

import event.EventMessage;

public class MouseButtonEvent extends EventMessage {
	
	public final int button;
	public final Type type;
	
	public enum Type {
		PRESSED,
		RELEASED
	}
	
	public MouseButtonEvent(int button,Type action) {
		this.button = button;
		this.type = action;
	}
	
	@Override
	public String toString() {
		return("MouseButtonEvent : " + button + " " + type);
	}
	
}
