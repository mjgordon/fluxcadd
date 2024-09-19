package controller;

import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;

public class UIEToggle extends UserInterfaceElement<UIEToggle> {

	public boolean state = true;


	public UIEToggle(String name, String displayName, int x, int y, int width, int height) {
		super(name, displayName, x, y, width, height);
	}


	@Override
	public UIEToggle pick(int x, int y) {
		if (super.pick(x, y) == this) {
			execute();
			return (this);
		}
		return (null);
	}


	@Override
	public void render() {
		OGLWrapper.fill(255, 255, 255);
		OGLWrapper.stroke(0, 0, 0);

		Primitives.rect(x, y, width, height);

		if (state) {
			OGLWrapper.fill(0, 0, 0);
			Primitives.rect(x + 3, y + 3, width - 6, height - 6);
		}

		BitmapFont.drawString(displayName, x + displayX, y + displayY, null);

		super.render();
	}


	@Override
	public void execute() {
		state = !state;
		super.execute();
	}


	@Override
	public void keyPressed(int key) {
	}


	@Override
	public void textInput(char character) {
	}

}
