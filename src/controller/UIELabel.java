package controller;

import fonts.BitmapFont;

public class UIELabel extends UserInterfaceElement<UIELabel> {

	public UIELabel(String name, String displayName, int x, int y, int width, int height) {
		super(name, displayName, x, y, width, height);
	}


	@Override
	public void render() {
		
		BitmapFont.drawString(displayName, x, y + (this.height / 2) - (BitmapFont.cellHeight / 2), true);

		super.render();
	}


	public void setText(String s) {
		this.displayName = s;
	}
}
