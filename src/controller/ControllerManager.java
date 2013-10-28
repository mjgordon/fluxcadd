package controller;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import static org.lwjgl.opengl.GL11.*;
public class ControllerManager implements Controllable {
	public ArrayList<Controller> controllers;
	
	public Controllable parent;
	
	public Controller keyboardTarget = null;
	
	public ControllerManager() {
		controllers = new ArrayList<Controller>();
	}
	
	public ControllerManager(Controllable parent) {
		this.parent = parent;
		controllers = new ArrayList<Controller>();
	}
	
	public void add(Controller c) {
		controllers.add(c);
		c.parent = this;
	}
	
	public void render() {
		glPushMatrix();
		glTranslatef(getX(),getY(),0);
		for (Controller c : controllers) c.render();
		glPopMatrix();
	}
	
	public void setParent(Controllable c) {
		parent = c;
	}
	
	public boolean poll() {
		boolean picked = false;
		keyboardTarget = null;
		for (Controller c : controllers) {
			if (c.pick(Mouse.getX() - getX(), Mouse.getY() - getY())) {
				picked = true;
				if (c instanceof Controller_TextField) {
					keyboardTarget = c;
				}
				else if (c instanceof Controller_Button) {
					parent.controllerEvent(c.name);
				}
			}
		}
		
		return(picked);
	}
	
	public void keyPressed() {
		if (keyboardTarget != null) {
			if (Keyboard.getEventKey() == Keyboard.KEY_TAB) {
				keyboardTarget.execute();
				keyboardTarget.selected = false;
				int id = controllers.indexOf(keyboardTarget);
				id ++;
				if (id >= controllers.size()) id = 0;
				keyboardTarget = controllers.get(id);
				keyboardTarget.selected = true;
			}
			keyboardTarget.keyPressed();
		}
	}
	
	public void setKeyboardTarget(Controller c) {
		keyboardTarget = c;
	}
	
	public int getX() {
		return(parent.getX());
	}
	
	public int getY() {
		return(parent.getY());
	}
	
	public int getWidth() {
		return(parent.getWidth());
	}
	
	public int getHeight() {
		return(parent.getHeight());
	}

	@Override
	public void controllerEvent(String name) {
		if (parent != null) parent.controllerEvent(name);
		else System.out.println("Null ControllerManager Parent");
		
	}
}
