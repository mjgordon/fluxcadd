package controller;

import fonts.BitmapFont;
import utility.Color3i;

public class UIELabel extends UserInterfaceElement<UIELabel> {

	private boolean dropShadow;


	public UIELabel(String name, String displayName, int x, int y, int width, int height) {
		super(name, displayName, x, y, width, height);
	}


	public UIELabel(String name, String displayName, int x, int y, int width, int height, boolean dropShadow) {
		super(name, displayName, x, y, width, height);
		this.dropShadow = dropShadow;
	}


	@Override
	public void render() {
		if (dropShadow) {
			BitmapFont.drawString(displayName, x, y + (this.height / 2) - (BitmapFont.cellHeight / 2), null, new Color3i(200, 200, 200));
		}
		else {
			BitmapFont.drawString(displayName, x, y + (this.height / 2) - (BitmapFont.cellHeight / 2), null);
		}

		super.render();
	}


	public void setText(String s) {
		this.displayName = s;
	}
}
