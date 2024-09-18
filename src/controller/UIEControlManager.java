package controller;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

/**
 * Manages a set of interface elements, handles selection between them and
 * redirects keyboard input to them
 *
 */
public class UIEControlManager {

	public UserInterfaceElement<? extends UserInterfaceElement<?>> keyboardTarget = null;

	private ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>> allElements;
	private ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>> currentLayer;

	private int width;
	private int height;

	private int currentX;
	private int currentY;

	private int topGutter = 50;
	private int leftGutter = 10;

	private int gutterX = 10;
	private int gutterY = 10;


	public UIEControlManager(int width, int height, int leftGutter, int topGutter, int gutterX, int gutterY) {
		this.width = width;
		this.height = height;
		this.currentX = leftGutter;
		this.currentY = topGutter;

		this.topGutter = topGutter;
		this.leftGutter = leftGutter;
		this.gutterX = gutterX;
		this.gutterY = gutterY;

		this.allElements = new ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>>();
		this.currentLayer = new ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>>();
	}


	public void setCurrentY(int y) {
		this.currentY = y;
	}


	public void add(UserInterfaceElement<? extends UserInterfaceElement<?>> uie) {
		if (uie.width == -1 || uie.fullWidth) {
			uie.fullWidth = true;
			uie.setWidth(this.width - 20);
		}

		if (currentX + uie.getLayoutWidth() > width) {
			newLine();
		}

		uie.setPosition(currentX, currentY);
		uie.reflow();

		currentLayer.add(uie);

		currentX += uie.getLayoutWidth();
		currentX += gutterX;

	}


	public void render() {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : allElements) {
			uie.render();
		}
	}


	public boolean poll(int mouseX, int mouseY) {

		boolean picked = false;
		keyboardTarget = null;
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : allElements) {
			UserInterfaceElement<? extends UserInterfaceElement<?>> pickResult = uie.pick(mouseX, mouseY);

			if (pickResult != null) {
				picked = true;
				if (pickResult instanceof UIETextField || pickResult instanceof UIETerminal) {
					keyboardTarget = pickResult;
				}
			}
		}
		return (picked);
	}


	public void keyPressed(int key) {
		if (keyboardTarget != null) {
			if (key == GLFW.GLFW_KEY_TAB) {
				keyboardTarget.execute();
				keyboardTarget.selected = false;
				int id = allElements.indexOf(keyboardTarget);
				id++;
				if (id >= allElements.size())
					id = 0;
				keyboardTarget = allElements.get(id);
				keyboardTarget.selected = true;
			}
			keyboardTarget.keyPressed(key);
		}
	}


	public void mouseDragged(int mouseButton, int dx, int dy) {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : allElements) {
			uie.mouseDragged(dx, dy);
		}
	}


	public void mouseReleased() {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : allElements) {
			uie.mouseReleased();
		}
	}
	
	public void mouseWheel(int delta) {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : allElements) {
			uie.mouseWheel(delta);
		}
	}


	public void textInput(char character) {
		if (keyboardTarget != null) {
			keyboardTarget.textInput(character);
		}
	}
	
	


	public void setKeyboardTarget(UserInterfaceElement<? extends UserInterfaceElement<?>> c) {
		keyboardTarget = c;
	}


	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}


	public void newLine() {
		currentX = leftGutter;

		int maxHeight = -1;
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : currentLayer) {
			if (uie.getHeight() > maxHeight) {
				maxHeight = uie.getLayoutHeight();
			}
		}
		currentY += maxHeight;
		currentY += gutterY;

		currentLayer.add(new UIENewLine());

		allElements.addAll(currentLayer);
		currentLayer.clear();
	}


	public void finalizeLayer() {
		allElements.addAll(currentLayer);
		currentLayer.clear();
	}


	public void reflow() {
		ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>> listCopy = new ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>>(allElements);
		allElements.clear();
		currentLayer.clear();

		this.currentX = leftGutter;
		this.currentY = topGutter;

		for (UserInterfaceElement<? extends UserInterfaceElement<?>> e : listCopy) {
			if (e instanceof UIENewLine) {
				newLine();
			}
			else {
				add(e);
			}
		}

		finalizeLayer();
	}
}
