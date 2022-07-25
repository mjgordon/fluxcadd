package controller;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.glfw.GLFW;

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
	
	private int topGutter = 50;
	private int leftGutter = 10;
	
	private int gutterX = 10;
	private int gutterY = 10;
	

	

	public UIEControlManager(int width, int height,int leftGutter, int topGutter, int gutterX, int gutterY) {
		this.width = width;
		this.height = height;
		this.currentX = leftGutter;
		this.currentY = topGutter;
		
		this.topGutter = topGutter;
		this.leftGutter = leftGutter;
		this.gutterX = gutterX;
		this.gutterY = gutterY;
		
		this.allElements = new ArrayList<UserInterfaceElement>();
		this.currentLayer = new ArrayList<UserInterfaceElement>();
	}
	
	public void setCurrentY(int y) {
		this.currentY = y;
	}

	public void add(UserInterfaceElement uie) {
		
		if (currentX + uie.getLayoutWidth() > width) {
			newLine();
		}
		
		uie.setPosition(currentX, currentY);
		
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
			UserInterfaceElement pickResult = uie.pick(mouseX, mouseY);
			
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
	
	public void textInput(char character) {
		if (keyboardTarget != null) {
			keyboardTarget.textInput(character);
		}
	}

	public void setKeyboardTarget(UserInterfaceElement c) {
		keyboardTarget = c;
	}

	
	public void newLine() {
		currentX = leftGutter;
		
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
		allElements.addAll(currentLayer);
		currentLayer.clear();
	}


}
