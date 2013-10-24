package data;

import java.util.ArrayList;

public class LispList extends LispData {
	
	public String name;
	public boolean nameComplete;
	
	public ArrayList<LispData> children;
	public LispData currentChild;
	
	
	public LispList(LispData parent) {
		this.parent = parent;
		name = "";
		nameComplete = false;
		children = new ArrayList<LispData>();
		currentChild = null;
	}
	
	public void receiveChar(char c) {
		if (currentChild == null) {
			if (c == '(') {
				currentChild = new LispList(this);
				children.add(currentChild);
			}
			else {
				currentChild = new LispSingle(this);
				children.add(currentChild);
				currentChild.receiveChar(c);
			}
		}
		else {
			if (c == ' ') {
				
			}
		}
	}
}
