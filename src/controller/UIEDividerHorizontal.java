package controller;

import graphics.Graphics2D;

public class UIEDividerHorizontal extends UserInterfaceElement<UIEDividerHorizontal> {

	public UIEDividerHorizontal(int width) {
		super("divider", "divider", -1, -1, width, 1);
		this.fullWidth = true;
	}
	
	@Override
	protected void render() {
		Graphics2D.stroke(0);
		Graphics2D.line(x, y, x + width, y);
	}
	

}
