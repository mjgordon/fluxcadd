package controller;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Manages a set of interface elements, handles selection between them and redirects keyboard input to them
 *
 */
public class UIEControlManager {
	public ArrayList<UserInterfaceElement> elements;


	public UserInterfaceElement keyboardTarget = null;

	public UIEControlManager() {
		elements = new ArrayList<UserInterfaceElement>();
	}

	public void add(UserInterfaceElement c) {
		elements.add(c);
	}

	public void render() {
		for (UserInterfaceElement c : elements)
			c.render();
	}


	public boolean poll(int mouseX, int mouseY) {
		boolean picked = false;
		keyboardTarget = null;
		for (UserInterfaceElement c : elements) {
			if (c.pick(mouseX, mouseY)) {
				picked = true;
				if (c instanceof UIETextField) {
					keyboardTarget = c;
				}
			}
		}
		return (picked);
	}

	public void keyPressed(int key) {
		if (keyboardTarget != null) {
			if (key == GLFW_KEY_TAB) {
				keyboardTarget.execute();
				keyboardTarget.selected = false;
				int id = elements.indexOf(keyboardTarget);
				id++;
				if (id >= elements.size())
					id = 0;
				keyboardTarget = elements.get(id);
				keyboardTarget.selected = true;
			}
			keyboardTarget.keyPressed(key);
		}
	}
	
	public void textInput(char character) {
		
		if (keyboardTarget != null) {
			keyboardTarget.textInput(character);
		}
	}

	public void setKeyboardTarget(UserInterfaceElement c) {
		keyboardTarget = c;
	}


}
