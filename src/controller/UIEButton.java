package controller;

import graphics.Graphics2D;
import utility.Color3i;

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
		Graphics2D.rect(x, y, width, height, Color3i.white, Color3i.black);

		Graphics2D.rect(x + 5, y + 5, width - 10, height - 10, null, Color3i.black);
		
		Graphics2D.text(x + displayX, y + displayY, displayName, true);

		super.render();
	}
}
