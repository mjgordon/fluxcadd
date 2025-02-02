package io;

import event.EventMessage;

public class MouseCursorEvent extends EventMessage {
	
	public final int x;
	public final int y;
	
	
	public MouseCursorEvent(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	
	@Override
	public String toString() {
		return "MouseCursorEvent : " + x + " " + y;
	}
}
