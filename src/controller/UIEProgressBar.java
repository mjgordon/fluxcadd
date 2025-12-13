package controller;

import graphics.Graphics2D;
import utility.Color3i;
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
	public void render() {
		Graphics2D.rect(x, y, width, height, Color3i.white, Color3i.black);

		int barWidth = (int) viewDomain.clip(viewDomain.convert(state, valueDomain));

		Graphics2D.rect(x, y, barWidth, height - 1, Color3i.blue, null);

		Graphics2D.text(x + displayX,  y + displayY, displayName, true);
		

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
