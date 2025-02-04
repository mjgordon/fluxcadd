package io;

import event.EventMessage;

public class MouseWheelEvent extends EventMessage {
	public final int x; 
	public final int y;
	public final int dx;
	public final int dy;
	
	
	public MouseWheelEvent(int x, int y, int dx, int dy) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
	
	@Override
	public String toString() {
		return "MouseWheelEvent : " + dx + " " + dy;
	}
}
