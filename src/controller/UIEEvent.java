package controller;

import event.EventMessage;

public class UIEEvent extends EventMessage {
	public UserInterfaceElement<? extends UserInterfaceElement<?>> element;
	
	public UIEEvent(UserInterfaceElement<? extends UserInterfaceElement<?>> element) {
		this.element = element;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
