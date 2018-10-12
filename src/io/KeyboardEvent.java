package io;

import event.EventMessage;

public class KeyboardEvent extends EventMessage {

	public final int key;
	public final int type;

	public KeyboardEvent(int key, int action) {
		this.key = key;
		this.type = action;
	}
	
	@Override
	public String toString() {
		return("KeyboardEvent : " + key + " " + type);
	}

}
