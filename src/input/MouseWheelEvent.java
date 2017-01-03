package input;

import event.EventMessage;

public class MouseWheelEvent extends EventMessage {
	
	public final int dx;
	public final int dy;
	
	public MouseWheelEvent(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
}
