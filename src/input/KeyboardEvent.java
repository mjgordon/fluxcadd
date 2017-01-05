package input;

import event.EventMessage;

public class KeyboardEvent extends EventMessage {

	public final int key;
	public final Type type;
	
	public enum Type {
		PRESSED,
		RELEASED;
	}

	public KeyboardEvent(int key, Type action) {
		this.key = key;
		this.type = action;
	}
	
	@Override
	public String toString() {
		return("KeyboardEvent : " + key + " " + type);
	}

}
