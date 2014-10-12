package ui;

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import fonts.PointFont;
import utility.Util;

import static org.lwjgl.opengl.GL11.*;

/**
 * 
 * @author Matt Gordon
 *
 */

public class Content_Terminal extends Content {
	
	public static ArrayList<String> strings = new ArrayList<String>();
	public static String currentString = "";
	
	public int listOrigin = 0;
	
	public Content_Terminal(Window parent) {
		this.parent = parent;
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
	
	public void keyPressed() {
		if (Keyboard.getEventKey() == Keyboard.KEY_BACK) {
			if (currentString.length() > 0) currentString=currentString.substring(0,currentString.length()-1);
		}
		else if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) execute();
		else currentString += Util.keyToChar();
	}

	public void mouseWheel(float amt) {
		listOrigin -= (amt/Math.abs(amt));
		if (listOrigin < 0) listOrigin = 0;
		if (listOrigin > strings.size()) listOrigin = strings.size();
	}
	
	public void addString(String s) {
		strings.add(s);
	}

	public void mousePressed() {}
	public void mouseDragged() {}

	
	

}
