package data;

import java.util.ArrayList;

public class LispData {
	
	public String identity;
	public ArrayList<LispData> children;
	public LispData parent;
	
	public LispData() {
		
	}
	
	/**
	 * Returns true if data is concluded by most recent character
	 * @param c
	 * @return
	 */
	public boolean receiveChar(char c) {
		if (c == '(') {
			
		}
		else if (c == ')') {
			return(true);
		}
		else if (c == ' ') {
			
		}
		
		return(false);
	}
	
}
