package lisp;

import java.util.ArrayList;



public class LispData {
	
	/** Identity is either the name of the function being called, or the actual data **/
	public String identity = "";
	public boolean identityDone = false;
	
	public ArrayList<LispData> children;
	public LispData parent;
	
	public int childState = 0;
	public static final int NOCHILD = 0;
	public static final int FUNCTIONCHILD = 1;
	public static final int DATACHILD = 2;
	
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
			if (identity.equals("") == false) childState = FUNCTIONCHILD;
		}
		else if (c == ')') {
			if (childState == FUNCTIONCHILD) childState = NOCHILD;
			else return(true);
		}
		else if (c == ' ') {
			if (childState == NOCHILD) {

				children.add(new LispData(this));
				childState = DATACHILD;
			}
			else if (childState == FUNCTIONCHILD) {
				children.get(children.size()-1).receiveChar(c);
			}
			else if (childState == DATACHILD) {
				children.add(new LispData(this));
			}
		}
		else {
			if (children.size() == 0) identity += c;
			else children.get(children.size()-1).receiveChar(c);
		}
		
		return(false);
	}
	
	public void printIdentity(int depth) {
		String out = "";
		for (int i = 0; i < depth; i++) out += " ";
		out += identity;
		System.out.println(out);
		for (LispData c : children) {
			c.printIdentity(depth + 1);
		}
	}
	
	public Object getData() {
		return( new Object());
	}
	
}
