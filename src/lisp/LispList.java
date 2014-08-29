package lisp;

import geometry.Point;

import java.util.ArrayList;

public class LispList extends LispData {
	
	public ArrayList<LispData> children;
	
	public boolean activeChild = false;
	
	public LispList(LispList parent) {
		super(parent);
		children = new ArrayList<LispData>();
	}

	public boolean receiveChar(char c) {
		if (c == '(') {
			if (children.size() > 0) {
				if (lastChild() instanceof LispList && activeChild) lastChild().receiveChar(c);
				else {
					children.add(new LispList(this));
					activeChild = true;
				}
			}
		}
		else if (c == ')') {
			if (lastChild() instanceof LispList) {
				if (lastChild().receiveChar(c)) activeChild = false;
				
			}
			else return(true);
		}
		else if (c == ' ') {
			if (lastChild() instanceof LispList && activeChild) lastChild().receiveChar(c);
			else activeChild = false;
		}
		else {
			if (activeChild == false) {
				children.add(new LispAtom(this));
				activeChild = true;
			}
			
			lastChild().receiveChar(c);
		}
		
		return(false);
	}
	
	public void printIdentity(int depth) {
		for (LispData d : children) d.printIdentity(depth+1);
	}
	

	public LispData lastChild() {
		if (children.size() == 0) return(null);
		else return(children.get(children.size()-1));
	}
	
	public Object getData() {
		if (children.get(0) instanceof LispAtom) {
			String name = ((LispAtom)children.get(0)).asString();
			if (name.equals("point")) {
				float x = 0,y = 0,z = 0;
				if (children.get(1) instanceof LispAtom) {
					x = ((LispAtom)children.get(1)).asFloat();
				}
				if (children.get(2) instanceof LispAtom) {
					y = ((LispAtom)children.get(2)).asFloat();
				}
				if (children.get(3) instanceof LispAtom) {
					z = ((LispAtom)children.get(3)).asFloat();
				}
				Point point = new Point(x,y,z);
				return(point);
			}
			else if (name.equals("add")) {
				float total = 0;
				for (int i = 1; i < children.size(); i++) {
					if (children.get(i) instanceof LispAtom) {
						total += ((LispAtom)children.get(1)).asFloat();
					}
				}
				return(total);
			}
		}
		return(null);
	}
}
