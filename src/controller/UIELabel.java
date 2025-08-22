package controller;

import graphics.Graphics2D;

public class UIELabel extends UserInterfaceElement<UIELabel> {

	public UIELabel(String name, String displayName, int x, int y, int width, int height) {
		super(name, displayName, x, y, width, height);
	}


	@Override
	public void render() {
		Graphics2D.text(x, y + (this.height / 2) - (12 / 2), displayName, true);

		super.render();
	}


	public void setText(String s) {
		this.displayName = s;
	}
}
