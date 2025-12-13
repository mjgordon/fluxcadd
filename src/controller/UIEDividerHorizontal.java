package controller;

import graphics.Graphics2D;
import utility.Color3i;

public class UIEDividerHorizontal extends UserInterfaceElement<UIEDividerHorizontal> {

	public UIEDividerHorizontal(int width) {
		super("divider", "divider", -1, -1, width, 1);
		this.fullWidth = true;
	}
	
	@Override
	protected void render() {
		Graphics2D.line(x, y, x + width, y, Color3i.black);
	}
	

}
