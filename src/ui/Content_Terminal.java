package ui;

import java.util.ArrayList;

import console.Console;
import console.ConsoleEvent;
import event.EventListener;
import event.EventMessage;
import fonts.PointFont;
import utility.Util;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * 
 * @author Matt Gordon
 *
 */

public class Content_Terminal extends Content implements EventListener {
	
	private ArrayList<String> strings = new ArrayList<String>();
	private String currentString = "";
	
	private int listOrigin = 0;
	
	public Content_Terminal(Panel parent) {
		super(parent);
		Console.instance().register(this);
	}
	
	public void execute() {
		currentString = currentString.toLowerCase();
		if (currentString.equals("screenshot")) Util.screenshot();
		strings.add(currentString);
		currentString = "";
		listOrigin = 0;
	}
	
	public void backspace() {
		if (currentString.length() > 0) {
			currentString =currentString.substring(0, currentString.length()-1);
		}
	}
	
	@Override
	public void render() {
		glColor3f(1,1,1);
		PointFont.drawString(currentString, getX() + 10, getY() + 9);
		glColor3f(0.7f,0.7f,0.7f);
			for (int i = 1 + listOrigin; i<=3 + listOrigin; i++) {
				int id = strings.size() -i;
				if (id < 0) continue;
				PointFont.drawString(strings.get(id),
								  getX() + 10,
								  getY() + (12 * (i+1-listOrigin)) -2 );
			}
	}
	
	private void addString(String s) {
		strings.add(s);
	}
	
	@Override
	protected void keyPressed(int key) {
		if (key == GLFW_KEY_BACKSPACE) {
			if (currentString.length() > 0) currentString=currentString.substring(0,currentString.length()-1);
		}
		else if (key == GLFW_KEY_ENTER || key == GLFW_KEY_KP_ENTER) execute();
	}
	
	@Override 
	protected void textInput(char character) {
		if (Character.isLetterOrDigit(character)) {
			currentString += character;
		}
	}

	@Override
	protected void mouseWheel(float amt) {
		listOrigin -= (amt/Math.abs(amt));
		if (listOrigin < 0) listOrigin = 0;
		if (listOrigin > strings.size()) listOrigin = strings.size();
	}
	
	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {}
	
	@Override
	protected void mouseDragged(int dx, int dy) {}

	@Override
	public void message(EventMessage message) {
		if (message instanceof ConsoleEvent) {
			ConsoleEvent event = (ConsoleEvent) message;
			addString(event.data);
		}
		
	}
}
