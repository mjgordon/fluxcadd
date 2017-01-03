package input;

import event.EventMessage;

public class MouseCursorEvent extends EventMessage {
	public final double x;
	public final double y;
	
	public MouseCursorEvent(double x, double y) {
		this.x = x;
		this.y = y;
	}
}
