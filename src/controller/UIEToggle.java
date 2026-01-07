package controller;

import graphics.Graphics2D;
import utility.Color3i;

public class UIEToggle extends UserInterfaceElement<UIEToggle> {

	public boolean state = true;


	public UIEToggle(String name, String displayName, int x, int y, int width, int height) {
		super(name, displayName, x, y, width, height);
	}


	@Override
	public UIEToggle pick(int x, int y) {
		if (super.pick(x, y) == this) {
			execute();
			return this;
		}
		return null;
	}


	@Override
	public void render() {
		Graphics2D.rect(x, y, width, height, Color3i.white, Color3i.black);

		if (state) {
			Graphics2D.rect(x + 3, y + 3, width - 6, height - 6, Color3i.black, Color3i.black);
		}

		Graphics2D.text(x + displayX, y + displayY, displayName, true);

		super.render();
	}


	@Override
	public void execute() {
		state = !state;
		super.execute();
	}
}
