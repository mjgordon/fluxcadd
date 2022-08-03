package controller;

import fonts.BitmapFont;

public class UIELabel extends UserInterfaceElement<UIELabel> {

	public UIELabel(Controllable target, String name, String displayName, int x, int y, int width, int height) {
		super(target, name, displayName, x, y, width,height);
	}

	@Override
	public void keyPressed(int key) {}

	@Override
	public void textInput(char character) {}

	@Override
	public void render() {
		BitmapFont.drawString(displayName, x, y + (this.height / 2) - (BitmapFont.cellHeight / 2), null);
		
		super.render();
	}
	
	public void setText(String s) {
		this.displayName = s;
	}

}
