package lisp;

import java.util.ArrayList;



public class LispData {
	
	/** Identity is either the name of the function being called, or the actual data **/
	public String identity;
	
	public ArrayList<LispData> children;
	public LispData parent;
	public boolean openChild = false;
	
	public LispData(LispData parent) {
		this.parent = parent;
		children = new ArrayList<LispData>();
	}
	
	// (point 10 10 10)
	// (point 10 (add 5 5) 10)
	
	/**
	 * Returns true if data is concluded by most recent character
	 * @param c
	 * @return
	 */
	public boolean receiveChar(char c) {
		if (c == '(') {
			openChild = true;
		}
		else if (c == ')') {
			if (openChild) openChild = false;
			else return(true);
		}
		else if (c == ' ') {
			if (openChild) children.add(new LispData(this));
			else children.get(children.size()-1).receiveChar(c);
		}
		else {
			if (children.size() == 0) identity += c;
			else if (children.get(children.size()-1).receiveChar(c)) {
				openChild = true;
			}
		}
		
		return(false);
	}
	
}
