package controller;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

import console.Console;
import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;
import utility.Util;
import utility.UtilString;

public class UIETerminal extends UserInterfaceElement<UIETerminal> {
	private ArrayList<String> strings = new ArrayList<String>();
	private String currentString = "";

	private int listOrigin = 0;


	public UIETerminal(String name, String displayName, int x, int y, int width, int height) {
		super(name, displayName, x, y, width, height);
	}


	@Override
	public void execute() {
		currentString = currentString.toLowerCase();
		strings.add(currentString);
		tempActions(currentString);
		currentString = "";
		listOrigin = 0;
	}


	// TODO: Simple action execution until proper terminal control
	private void tempActions(String s) {
		if (currentString.equals("screenshot")) {
			Util.screenshot();
		}
		else if (currentString.equals("debug_gui")) {
			UserInterfaceElement.debugOutlines = !UserInterfaceElement.debugOutlines;
			Console.log("UIE debug flag : " + UserInterfaceElement.debugOutlines);
		}

	}


	@Override
	public void render() {
		OGLWrapper.fill(255, 255, 255);
		if (selected) {
			OGLWrapper.stroke(0, 0, 255);
		}

		else {
			OGLWrapper.stroke(0, 0, 0);
		}
		Primitives.rect(x, y, width, height);

		OGLWrapper.noFill();
		OGLWrapper.stroke(0xFFFFFF);
		Primitives.rect(x + 1, y + 1, width - 2, height - 2);

		GL11.glColor3f(1, 1, 1);
		BitmapFont.drawString("> " + currentString, x, y + height - 12, null);
		GL11.glColor3f(0.7f, 0.7f, 0.7f);
		for (int i = 1 + listOrigin; i <= 3 + listOrigin; i++) {
			int id = strings.size() - i;
			if (id < 0) {
				continue;
			}
			BitmapFont.drawString(strings.get(id), x + 16, y + height - (12 * (i + 1) + 4), null);
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
		if (key == GLFW.GLFW_KEY_BACKSPACE) {
			if (currentString.length() > 0)
				currentString = currentString.substring(0, currentString.length() - 1);
		}
		else if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
			execute();
		}

	}


	@Override
	public void textInput(char character) {
		if (UtilString.isPrintableChar(character)) {
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
