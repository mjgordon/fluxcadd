package controller;

import java.util.ArrayList;


public class UIEVerticalStack extends UserInterfaceElement<UIEVerticalStack> {

	private ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>> elements;

	private int yOffset;


	public UIEVerticalStack(String name, String displayName, int x, int y, int width, int height) {
		super(name, displayName, x, y, width, height);
		elements = new ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>>();

		debugOutlineColor = 0x00FF00;
	}


	@Override
	public void keyPressed(int key) {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : elements) {
			uie.keyPressed(key);
		}
	}


	@Override
	public void textInput(char character) {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : elements) {
			uie.textInput(character);
		}
	}
	
	
	@Override
	public void mouseDragged(int x, int y, int dx, int dy) {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : elements) {
			uie.mouseDragged(x, y, dx,dy);
		}
	}
	
	
	@Override
	public void mouseReleased() {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : elements) {
			uie.mouseReleased();
		}
	}


	@Override
	public void render() {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : elements) {
			uie.render();
		}
		super.render();
	}


	@Override
	public void setPosition(int x, int y) {
		super.setPosition(x, y);
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : elements) {
			uie.x = this.x;
			uie.y += this.y;
		}
	}


	@Override
	public UserInterfaceElement<? extends UserInterfaceElement<?>> pick(int mouseX, int mouseY) {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : elements) {
			if (uie.pick(mouseX, mouseY) == uie) {
				return (uie);
			}
		}
		return (null);
	}


	public void add(UserInterfaceElement<? extends UserInterfaceElement<?>> uie) {
		uie.x = this.x;
		uie.y = yOffset;

		yOffset += uie.getLayoutHeight();

		elements.add(uie);
	}


	public void close() {
		this.height = yOffset;
	}
	
	
	public void reflow() {
		yOffset = this.y;
		
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : elements) {
			uie.x = this.x;
			uie.y = yOffset;
			
			yOffset += uie.getLayoutHeight();
		}
	}
}
