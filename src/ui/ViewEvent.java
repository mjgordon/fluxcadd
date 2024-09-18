package ui;

import event.EventMessage;

public class ViewEvent extends EventMessage {
	public enum ViewEventType {
			KEYBOARD,
			MOUSE,
			MOUSE_PRESSED,
			MOUSE_DRAGGED,
			MOUSE_WHEEL
	}
	
	public ViewEventType type;
	
	public ViewEvent (ViewEventType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type.toString();
	}
	
	public interface ViewEventListener {
		public abstract void receiveMessage(ViewEvent ve);
	}
}
