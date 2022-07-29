package controller;

import utility.Util;
import static org.lwjgl.glfw.GLFW.*;

import fonts.BitmapFont;
import graphics.Primitives;

public class UIETextField extends UserInterfaceElement {

	public String currentString = "";

	private int highlight = 0xFFFFFF;

	private boolean clearOnExecute = true;


	public UIETextField(Controllable target, String name, String displayName, int x, int y, int width, int height) {
		super(target, name, displayName, x, y, width, height);
	}

//	public UIETextField(Controllable target,String name,String displayName,MutableVariable target, int x, int y, int width, int height) {
//		super(target,name,x,y,width,height);
//		this.target = target;
//		this.displayName = displayName;
//		currentString = target.toString();
//	}


	public void keyPressed(int key) {
		if (key == GLFW_KEY_ENTER || key == GLFW_KEY_KP_ENTER) {
			execute();
		}
		else if (key == GLFW_KEY_BACKSPACE) {
			if (currentString.length() > 0)
				currentString = currentString.substring(0, currentString.length() - 1);
		}
	}


	@Override
	public void textInput(char character) {
		if (Character.isLetterOrDigit(character)) {
			currentString += character;
		}
	}


	public void setValue(String s) {
		this.currentString = s;
		execute();
	}


	public void setValueSilent(String s) {
		this.currentString = s;
	}


	public String getValue() {
		return (this.currentString);
	}


	@Override
	public UserInterfaceElement pick(int x, int y) {
		UserInterfaceElement picked = super.pick(x, y);
		selected = (picked != null);
		return (picked);
	}


	@Override
	public void execute() {
		super.execute();
		
		if (clearOnExecute) {
			this.currentString = "";	
		}
		
	}


	@Override
	public void render() {
		Util.fill(255, 255, 255);
		if (selected) {
			Util.stroke(0, 0, 255);
		}
		else {
			Util.stroke(0, 0, 0);
		}
		Primitives.rect(x, y, width, height);

		Util.noFill();
		Util.stroke(highlight);
		Primitives.rect(x + 1, y + 1, width - 2, height - 2);

		Util.color(0, 0, 0);
		BitmapFont.drawString(currentString, x + 3, y + 5, null);
		BitmapFont.drawString(displayName, x + displayX, y + displayY, null);
		
		super.render();
	}
	
	public UIETextField setClearOnExecute(boolean clear) {
		this.clearOnExecute = clear;
		return(this);
	}

}
