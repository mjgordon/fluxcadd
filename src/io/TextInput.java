package io;

import event.EventManager;

public class TextInput extends EventManager<TextInputEvent> {

	private static TextInput instance;
	
	protected TextInput() {
		super();
	}
	
	public void textInputEvent(TextInputEvent e) {
		sendMessage(e);
	}
	
	
	public static TextInput instance() {
		if (instance == null) {
			instance = new TextInput();
		}
		return(instance);
	}
}
