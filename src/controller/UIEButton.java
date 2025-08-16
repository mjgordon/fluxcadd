package controller;


import fonts.BitmapFont;
import graphics.Graphics2D;

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
		Graphics2D.fill(255, 255, 255);
		Graphics2D.stroke(0, 0, 0);

		Graphics2D.rect(x, y, width, height);

		Graphics2D.noFill();

		Graphics2D.rect(x + 5, y + 5, width - 10, height - 10);

		BitmapFont.drawString(displayName, x + displayX, y + displayY, true);

		super.render();
	}
}
