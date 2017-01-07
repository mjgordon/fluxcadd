package input;

import event.EventMessage;

public class TextInputEvent extends EventMessage {

	public int codepoint;
	
	public TextInputEvent(int codepoint) {
		this.codepoint = codepoint;
	}
	
	@Override
	public String toString() {
		return("TextInputEvent: " + codepoint);
	}

}
