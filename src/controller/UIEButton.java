package controller;

import fonts.BitmapFont;
import graphics.Primitives;
import utility.Util;

public class UIEButton extends UserInterfaceElement {
	public UIEButton(Controllable target,String name,String displayName, int x, int y, int width, int height) {
		super(target, name,displayName,x,y,width,height);
	}
	
	public UserInterfaceElement pick (int x, int y) {
		if (super.pick(x,y) == this) {
			execute();
			return(this);
		}
		return(null);
	}

	@Override
	public void render() {
		Util.fill(255,255,255);
		Util.stroke(0,0,0);
		
		Primitives.rect(x, y, width, height);
		
		Util.noFill();
		
		Primitives.rect(x + 5, y + 5, width - 10, height - 10);
		
		BitmapFont.drawString(displayName, x + displayX, y + displayY,null);
		
		super.render();
	}
	

	@Override
	public void keyPressed(int key) {}
	
	@Override
	public void textInput(char character) {}
}
