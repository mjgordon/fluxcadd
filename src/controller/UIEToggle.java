package controller;

import fonts.BitmapFont;
import utility.Util;

public class UIEToggle extends UserInterfaceElement {

	public boolean state = true;

	public UIEToggle(Controllable target, String name, String displayName, int x, int y, int width, int height) {
		super(target, name,displayName, x, y, width, height);
	}

	public boolean pick(int x, int y) {
		if (super.pick(x, y)) {
			execute();
			return (true);
		}
		return (false);
	}

	@Override
	public void render() {
		Util.fill(255, 255, 255);
		Util.stroke(0, 0, 0);

		Util.rect(x, y, width, height);

		Util.noFill();

		if (state) {
			Util.line(x, y, x + width, y + height);
			Util.line(x + width, y, x, y + height);
		}

		BitmapFont.drawString(displayName, x + displayX, y + displayY,null);
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
