package utility;

import java.awt.event.KeyEvent;

public class UtilString {
	
	public static String leftPad(String s, int length) {
		while(s.length() < length) {
			s = "0" + s;
		}
		return s;
	}

	public static boolean isPrintableChar(char c) {
		Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
		return (!Character.isISOControl(c)) && c != KeyEvent.CHAR_UNDEFINED && block != null && block != Character.UnicodeBlock.SPECIALS;
	}

}
