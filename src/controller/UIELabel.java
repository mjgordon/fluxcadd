package controller;

import event.EventListener;
import fonts.BitmapFont;
import utility.Color;

public class UIELabel extends UserInterfaceElement<UIELabel> {
	
	private boolean dropShadow;

	public UIELabel(EventListener target, String name, String displayName, int x, int y, int width, int height) {
		super(target, name, displayName, x, y, width, height);
	}
	
	public UIELabel(EventListener target, String name, String displayName, int x, int y, int width, int height, boolean dropShadow) {
		super(target, name, displayName, x, y, width, height);
		this.dropShadow = dropShadow;
	}


	@Override
	public void keyPressed(int key) {
	}


	@Override
	public void textInput(char character) {
	}


	@Override
	public void render() {
		if (dropShadow) {
			BitmapFont.drawString(displayName, x, y + (this.height / 2) - (BitmapFont.cellHeight / 2), null, new Color(200,200,200));
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
