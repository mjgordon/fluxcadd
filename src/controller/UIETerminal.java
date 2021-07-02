package controller;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER;
import static org.lwjgl.opengl.GL11.glColor3f;

import java.util.ArrayList;

import fonts.BitmapFont;
import graphics.Primitives;
import utility.Util;

public class UIETerminal extends UserInterfaceElement {
	private ArrayList<String> strings = new ArrayList<String>();
	private String currentString = "";

	private int listOrigin = 0;

	public UIETerminal(Controllable target, String name, String displayName, int x, int y, int width, int height) {
		super(target, name, displayName, x, y, width, height);
	}

	@Override
	public void execute() {
		currentString = currentString.toLowerCase();
		if (currentString.equals("screenshot"))
			Util.screenshot();
		strings.add(currentString);
		currentString = "";
		listOrigin = 0;
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
		Util.stroke(0xFFFFFF);
		Primitives.rect(x + 1, y + 1, width - 2, height - 2);

		glColor3f(1, 1, 1);
		BitmapFont.drawString("> " + currentString, x, y + height - 12, null);
		glColor3f(0.7f, 0.7f, 0.7f);
		for (int i = 1 + listOrigin; i <= 3 + listOrigin; i++) {
			int id = strings.size() - i;
			if (id < 0) {
				continue;
			}
			BitmapFont.drawString(strings.get(id), x + 16, y + height - (12 * (i + 1) + 4),null);
		}
		
		BitmapFont.drawString(displayName, x + displayX, y + displayY, null);
	}

	public void backspace() {
		if (currentString.length() > 0) {
			currentString = currentString.substring(0, currentString.length() - 1);
		}
	}

	public void addString(String s) {
		strings.add(s);
	}

	@Override
	public void keyPressed(int key) {
		if (key == GLFW_KEY_BACKSPACE) {
			if (currentString.length() > 0)
				currentString = currentString.substring(0, currentString.length() - 1);
		} 
		else if (key == GLFW_KEY_ENTER || key == GLFW_KEY_KP_ENTER) {
			execute();
		}
			
	}

	@Override
	public void textInput(char character) {
		if (Character.isLetterOrDigit(character)) {
			currentString += character;
		}
	}

	public void mouseWheel(float amt) {
		listOrigin -= (amt / Math.abs(amt));
		if (listOrigin < 0)
			listOrigin = 0;
		if (listOrigin > strings.size())
			listOrigin = strings.size();
	}

}
