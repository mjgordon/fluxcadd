package controller;

import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;

public class UIEButton extends UserInterfaceElement<UIEButton> {
	public UIEButton(String name, String displayName, int x, int y, int width, int height) {
		super(name, displayName, x, y, width, height);
	}


	@Override
	public UIEButton pick(int x, int y) {
		if (super.pick(x, y) != null) {
			execute();
			return this;
		}
		return (null);
	}


	@Override
	public void render() {
		OGLWrapper.fill(255, 255, 255);
		OGLWrapper.stroke(0, 0, 0);

		Primitives.rect(x, y, width, height);

		OGLWrapper.noFill();

		Primitives.rect(x + 5, y + 5, width - 10, height - 10);

		BitmapFont.drawString(displayName, x + displayX, y + displayY, true);

		super.render();
	}
}
