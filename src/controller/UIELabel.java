package controller;

import fonts.BitmapFont;

public class UIELabel extends UserInterfaceElement {

	public UIELabel(Controllable target, String name, String displayName, int x, int y) {
		super(target, name, displayName, x, y, 0, 0);
	}

	@Override
	public void keyPressed(int key) {}

	@Override
	public void textInput(char character) {}

	@Override
	public void render() {
		BitmapFont.drawString(displayName, x, y, null);
		
	}
	
	public void setText(String s) {
		this.displayName = s;
	}

}
