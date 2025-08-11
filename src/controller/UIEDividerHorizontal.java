package controller;

import graphics.OGLWrapper;
import graphics.Primitives;

public class UIEDividerHorizontal extends UserInterfaceElement<UIEDividerHorizontal> {

	public UIEDividerHorizontal(int width) {
		super("divider", "divider", -1, -1, width, 1);
		this.fullWidth = true;
	}
	
	@Override
	protected void render() {
		OGLWrapper.stroke(0);
		Primitives.line(x, y, x + width, y);
	}
	

}
