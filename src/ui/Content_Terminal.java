package ui;

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import fonts.PointFont;
import utility.Util;

import static org.lwjgl.opengl.GL11.*;
public class Content_Terminal extends WindowContent {
	
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
	}
	
	public void backspace() {
		if (currentString.length() > 0) {
			currentString =currentString.substring(0, currentString.length()-1);
		}
	}
	
	public void render() {
		glColor3f(1,1,1);
		PointFont.drawString(currentString, parent.x + 10, parent.y + 9);
		glColor3f(0.7f,0.7f,0.7f);
			for (int i = 1 + listOrigin; i<=3 + listOrigin; i++) {
				int id = strings.size() -i;
				if (id < 0) continue;
				PointFont.drawString(strings.get(id),
								  parent.x + 10,
								  parent.y + (12 * (i+1-listOrigin)) -2 );
			}
	}
	
	public void keyPressed() {
		int id = Keyboard.getEventKey();

		     if (id==Keyboard.KEY_0) currentString+="0";
		else if (id==Keyboard.KEY_1) currentString+="1";
		else if (id==Keyboard.KEY_2) currentString+="2";
		else if (id==Keyboard.KEY_3) currentString+="3";
		else if (id==Keyboard.KEY_4) currentString+="4";
		else if (id==Keyboard.KEY_5) currentString+="5";
		else if (id==Keyboard.KEY_6) currentString+="6";
		else if (id==Keyboard.KEY_7) currentString+="7";
		else if (id==Keyboard.KEY_8) currentString+="8";
		else if (id==Keyboard.KEY_9) currentString+="9";
		
		else if (id==Keyboard.KEY_A) currentString+="A";
		else if (id==Keyboard.KEY_B) currentString+="B";
		else if (id==Keyboard.KEY_C) currentString+="C";
		else if (id==Keyboard.KEY_D) currentString+="D";
		else if (id==Keyboard.KEY_E) currentString+="E";
		else if (id==Keyboard.KEY_F) currentString+="F";
		else if (id==Keyboard.KEY_G) currentString+="G";
		else if (id==Keyboard.KEY_H) currentString+="H";
		else if (id==Keyboard.KEY_I) currentString+="I";
		else if (id==Keyboard.KEY_J) currentString+="J";
		else if (id==Keyboard.KEY_K) currentString+="K";
		else if (id==Keyboard.KEY_L) currentString+="L";
		else if (id==Keyboard.KEY_M) currentString+="M";
		else if (id==Keyboard.KEY_N) currentString+="N";
		else if (id==Keyboard.KEY_O) currentString+="O";
		else if (id==Keyboard.KEY_P) currentString+="P";
		else if (id==Keyboard.KEY_Q) currentString+="Q";
		else if (id==Keyboard.KEY_R) currentString+="R";
		else if (id==Keyboard.KEY_S) currentString+="S";
		else if (id==Keyboard.KEY_T) currentString+="T";
		else if (id==Keyboard.KEY_U) currentString+="U";
		else if (id==Keyboard.KEY_V) currentString+="V";
		else if (id==Keyboard.KEY_W) currentString+="W";
		else if (id==Keyboard.KEY_X) currentString+="X";
		else if (id==Keyboard.KEY_Y) currentString+="Y";
		else if (id==Keyboard.KEY_Z) currentString+="Z";
		
		else if (id==Keyboard.KEY_MINUS) currentString+="-";
		else if (id==Keyboard.KEY_SPACE) currentString+=" ";
		else if (id==Keyboard.KEY_BACK) {
			if (currentString.length() > 0) currentString=currentString.substring(0,currentString.length()-1);
		}
		else if (id==Keyboard.KEY_RETURN) execute();
	}

	public void mouseWheel(float amt) {
		System.out.println(amt);
		listOrigin -= amt;
		if (listOrigin < 0) listOrigin = 0;
		if (listOrigin > strings.size()) listOrigin = strings.size();
	}

	@Override
	public void mouseDragged() {}
	
	

}
