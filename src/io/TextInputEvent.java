package io;

import event.EventMessage;

public class TextInputEvent extends EventMessage {

	public char character;
	
	
	public TextInputEvent(char character) {
		this.character = character;
	}
	
	
	@Override
	public String toString() {
		return "TextInputEvent: " + character;
	}
}
