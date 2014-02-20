package lisp;

import geometry.Line;
import geometry.Point;

import java.util.ArrayList;



public class LispData {
	
	/** Identity is either the name of the function being called, or the actual data **/
	public String identity = "";
	public boolean identityDone = false;
	
	public ArrayList<LispData> children;
	public LispData parent;
	
	public int type = FUNCTION;
	public static final int FUNCTION = 0;
	public static final int DATA = 1;
	
	public int childState = NOCHILD;
	public static final int NOCHILD = 0;
	public static final int FUNCTIONCHILD = 1;
	public static final int DATACHILD = 2;
	
	public LispData(LispData parent) {
		this.parent = parent;
		children = new ArrayList<LispData>();
	}
	
	//    (point (add (add 2 1) (add 1 2)) 20 20)
	
	/**
	 * Returns true if data is concluded by most recent character
	 * @param c
	 * @return
	 */
	public boolean receiveChar(char c) {
		if (c == '(') {
			if (identity.equals("") == false) {
				if (childState == FUNCTIONCHILD) {
					lastChild().receiveChar(c);
				}
				else {
					childState = FUNCTIONCHILD;
					lastChild().type = FUNCTION;
				}
	
			}
			
		}
		else if (c == ')') {
			if (childState == FUNCTIONCHILD) {
				if (lastChild().receiveChar(c)) {
					childState = NOCHILD;
				}
			}
			else return(true);
		}
		else if (c == ' ') {
			if (childState == NOCHILD) {

				children.add(new LispData(this));
				childState = DATACHILD;
				lastChild().type = DATA;
				
			}
			else if (childState == FUNCTIONCHILD) {
				lastChild().receiveChar(c);
			}
			else if (childState == DATACHILD) {
				children.add(new LispData(this));
				lastChild().type = DATA;
			}
		}
		else {
			if (children.size() == 0) identity += c;
			else lastChild().receiveChar(c);
		}
		
		return(false);
	}
	
	public void printIdentity(int depth) {
		String out = "";
		for (int i = 0; i < depth; i++) out += ".";
		out += identity + " " + getType();
		System.out.println(out);
		for (LispData c : children) {
			c.printIdentity(depth + 1);
		}
	}
	
	public String getType() {
		if (type == FUNCTION) return("function");
		else if (type == DATA) return("data");
		else return("none");
	}
	
	public LispData lastChild() {
		return(children.get(children.size()-1));
	}
	
	public Object getData() {
		if (type == DATA) {
			return(identity);
		}
		else if (type == FUNCTION) {
			if (identity.equals("point")) {
				Object x = children.get(0).getData();
				Object y = children.get(1).getData();
				Object z = children.get(2).getData();
				if (x instanceof String && y instanceof String && z instanceof String) {
					return(new Point(Float.valueOf((String)x),Float.valueOf((String)y),Float.valueOf((String)z)));
				}
			}
			else if (identity.equals("line")) {
				Object a = children.get(0).getData();
				Object b = children.get(1).getData();
				if (a instanceof Point && b instanceof Point) {
					return(new Line((Point)a,(Point)b));
				}
			}
			else if (identity.equals("add")) {
				Object a = children.get(0).getData();
				Object b = children.get(1).getData();
				if (a instanceof String && b instanceof String) {
					return(Float.valueOf((String)a) + Float.valueOf((String)b) + "");
				}
			}
			else if (identity.equals("subtract")) {
				Object a = children.get(0).getData();
				Object b = children.get(1).getData();
				if (a instanceof String && b instanceof String) {
					return(Float.valueOf((String)a) - Float.valueOf((String)b) + "");
				}
			}
			else if (identity.equals("multiply")) {
				Object a = children.get(0).getData();
				Object b = children.get(1).getData();
				if (a instanceof String && b instanceof String) {
					return(Float.valueOf((String)a) * Float.valueOf((String)b) + "");
				}
			}
			else if (identity.equals("divide")) {
				Object a = children.get(0).getData();
				Object b = children.get(1).getData();
				if (a instanceof String && b instanceof String) {
					return(Float.valueOf((String)a) / Float.valueOf((String)b) + "");
				}
			}
			return( new Object());
		}
		else {
			System.out.println("Something went wrong");
			return(new Object());
		}

	}
	
}
