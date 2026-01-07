package controller;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import console.Console;
import graphics.Graphics2D;
import utility.Color3i;
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
		Graphics2D.rect(x, y, width, height, Color3i.white, selected ? Color3i.blue : Color3i.black);

		Graphics2D.rect(x + 1, y + 1, width - 2, height - 2, null, Color3i.white);

		Graphics2D.text(x, y + height - 12, "> " + currentString, true);
		for (int i = 1 + listOrigin; i <= 3 + listOrigin; i++) {
			int id = strings.size() - i;
			if (id < 0) {
				continue;
			}
			Graphics2D.text(x + 16, y + height - (12 * (i + 1) + 4), strings.get(id), true);
		}

		Graphics2D.text(x + displayX, y + displayY, displayName, true);
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
