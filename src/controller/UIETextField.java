package controller;

import fonts.PointFont;
import utility.Util;
import static org.lwjgl.glfw.GLFW.*;

public class UIETextField extends UserInterfaceElement {
	
	public String currentString = "";
	
	public int highlight = 0xFFFFFF;
	
	//public MutableVariable target;
	
	public UIETextField(Controllable target,String name,String displayName, int x, int y, int width, int height) {
		super(target,name,displayName,x,y,width,height);
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
		else if (key == GLFW_KEY_BACKSPACE){ 
			if (currentString.length() > 0) currentString = currentString.substring(0, currentString.length() - 1);
		}
	}
	
	@Override
	public void textInput(char character) {
		if (Character.isLetterOrDigit(character)) {
			currentString += character;
		}
	}
	
	public boolean pick(int x, int y) {
		boolean picked = super.pick(x,y);
		selected = picked;
		return(picked);
	}
	
//	@Override
//	public void execute() {
//		if (target != null) {
//			try {
//				target.set(currentString);
//				highlight = 0xFFFFFF;
//			}
//			catch(NumberFormatException e) {
//				highlight = 0xFF4444;
//			}
//		}
//		super.execute();
//	}
	
	@Override
	public void render() {
		Util.fill(255,255,255);
		if (selected) Util.stroke(0,0,255);
		else Util.stroke(0,0,0);
		Util.rect(x, y, width, height);	
		
		Util.noFill();
		Util.stroke(highlight);
		Util.rect(x+1,y+1,width-2,height-2);
		
		Util.color(0,0,0);
		PointFont.drawString(currentString, x + 3, y + 5);
		PointFont.drawString(displayName, x, y + 22);
	}
	
	

}
