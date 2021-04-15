package controller;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;

public class ControllerManager implements Controllable {
	public ArrayList<Controller> controllers;

	public Controllable parent;

	public Controller keyboardTarget = null;

	public ControllerManager(Controllable parent) {
		this.parent = parent;
		controllers = new ArrayList<Controller>();
	}

	public void add(Controller c) {
		controllers.add(c);
		c.parent = this;
	}

	public void render() {
		for (Controller c : controllers)
			c.render();
	}

	public void setParent(Controllable c) {
		parent = c;
	}

	public boolean poll(int mouseX, int mouseY) {
		boolean picked = false;
		keyboardTarget = null;
		for (Controller c : controllers) {
			if (c.pick(mouseX - getX(), mouseY - getY())) {
				picked = true;
				if (c instanceof Controller_TextField) {
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
				int id = controllers.indexOf(keyboardTarget);
				id++;
				if (id >= controllers.size())
					id = 0;
				keyboardTarget = controllers.get(id);
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

	public void setKeyboardTarget(Controller c) {
		keyboardTarget = c;
	}

	public int getX() {
		return (parent.getX());
	}

	public int getY() {
		return (parent.getY());
	}

	public int getWidth() {
		return (parent.getWidth());
	}

	public int getHeight() {
		return (parent.getHeight());
	}

	@Override
	public void controllerEvent(Controller controller) {
		if (parent != null)
			parent.controllerEvent(controller);
		else {
			System.out.println("ControllerManager has no assigned parent");
		}

	}

}
