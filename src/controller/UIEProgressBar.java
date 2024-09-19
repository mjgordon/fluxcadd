package controller;

import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;
import utility.math.Domain;

public class UIEProgressBar extends UserInterfaceElement<UIEProgressBar> {

	private Domain valueDomain;
	private Domain viewDomain;

	private float state = 0;


	public UIEProgressBar(String name, String displayName, int x, int y, int width, int height, float max) {
		super(name, displayName, x, y, width, height);
		this.valueDomain = new Domain(0, max);
		this.viewDomain = new Domain(0, width);
	}


	@Override
	public void keyPressed(int key) {
	}


	@Override
	public void textInput(char character) {
	}


	@Override
	public void render() {
		OGLWrapper.fill(255, 255, 255);
		OGLWrapper.stroke(0, 0, 0);
		Primitives.rect(x, y, width, height);

		int barWidth = (int) viewDomain.clip(viewDomain.convert(state, valueDomain));

		OGLWrapper.fill(0, 0, 255);
		OGLWrapper.noStroke();

		Primitives.rect(x, y, barWidth, height - 1);

		BitmapFont.drawString(displayName, x + displayX, y + displayY, null);

		super.render();
	}


	public void update(float state) {
		this.state = state;
	}


	public void setWidth(int width) {
		super.setWidth(width);
		viewDomain = new Domain(0, width);
	}
}
