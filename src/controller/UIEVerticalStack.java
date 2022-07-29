package controller;

import java.util.ArrayList;

public class UIEVerticalStack extends UserInterfaceElement {
	
	private ArrayList<UserInterfaceElement> elements;
	
	private int yOffset;

	public UIEVerticalStack(Controllable target, String name, String displayName, int x, int y, int width, int height) {
		super(target, name, displayName, x, y, width,height);
		elements = new ArrayList<UserInterfaceElement>();
		
		debugOutlineColor = 0x00FF00;
	}

	@Override
	public void keyPressed(int key) {
		for (UserInterfaceElement uie : elements) {
			uie.keyPressed(key);
		}
		
	}

	@Override
	public void textInput(char character) {
		for (UserInterfaceElement uie : elements) {
			uie.textInput(character);
		}
	}

	@Override
	public void render() {
		for (UserInterfaceElement uie : elements) {
			uie.render();
		}
		
		super.render();
	}
	
	@Override
	public void setPosition(int x, int y) {
		super.setPosition(x, y);
		for (UserInterfaceElement uie : elements) {
			uie.x = this.x;
			uie.y += this.y;
		}
	}
	
	
	@Override
	public UserInterfaceElement pick(int mouseX, int mouseY) {
		for (UserInterfaceElement uie : elements) {
			if (uie.pick(mouseX, mouseY) == uie) {
				return(uie);
			}
		}
		return(null);
	}
	
	public void add(UserInterfaceElement uie) {
		uie.x = this.x;
		uie.y = yOffset;
		
		yOffset += uie.getLayoutHeight();
		
		elements.add(uie);
	}
	
	
	public void close() {
		this.height = yOffset;
	}

}
