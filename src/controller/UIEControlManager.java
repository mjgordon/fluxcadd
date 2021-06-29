package controller;

import java.util.ArrayList;

import org.joml.Math;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Manages a set of interface elements, handles selection between them and redirects keyboard input to them
 *
 */
public class UIEControlManager {


	public UserInterfaceElement keyboardTarget = null;
	
	private ArrayList<UserInterfaceElement> allElements; 
	private ArrayList<UserInterfaceElement> currentLayer;
	
	private int width;
	private int height;
	
	private int currentX;
	private int currentY;
	
	private int gutterX = 10;
	private int gutterY = 10;
	

	

	public UIEControlManager(int width, int height) {
		
		this.width = width;
		this.height = height;
		this.currentX = gutterX;
		this.currentY = 50;
		
		this.allElements = new ArrayList<UserInterfaceElement>();
		this.currentLayer = new ArrayList<UserInterfaceElement>();
	}

	public void add(UserInterfaceElement uie) {
		uie.x = currentX;
		uie.y = currentY;
		
		if (currentX + uie.getLayoutWidth() > width) {
			newLine();
		}
		
		currentLayer.add(uie);
		
		currentX += uie.getLayoutWidth();
		currentX += gutterX;
		
	}

	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, height, 0);
		GL11.glScalef(1,-1, 1);		

		

		for (UserInterfaceElement uie : allElements) {
			uie.render();
		}
		GL11.glPopMatrix();
			
	}


	public boolean poll(int mouseX, int mouseY) {
		mouseY = height - mouseY;
		
		boolean picked = false;
		keyboardTarget = null;
		for (UserInterfaceElement uie : allElements) {
			if (uie.pick(mouseX, mouseY)) {
				picked = true;
				if (uie instanceof UIETextField) {
					keyboardTarget = uie;
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
	
	public void textInput(char character) {
		
		if (keyboardTarget != null) {
			keyboardTarget.textInput(character);
		}
	}

	public void setKeyboardTarget(UserInterfaceElement c) {
		keyboardTarget = c;
	}
	

	
	public void addUIE(UserInterfaceElement uie) {
		
		
		
	}
	
	public void newLine() {
		currentX = gutterX;
		
		int maxHeight = -1;
		for (UserInterfaceElement uie : currentLayer) {
			if (uie.getHeight() > maxHeight) {
				maxHeight = uie.getLayoutHeight();
			}
		}
		currentY += maxHeight;
		currentY += gutterY;
		
		allElements.addAll(currentLayer);
		currentLayer.clear();
	}
	
	public void finalize() {
		
	}


}
