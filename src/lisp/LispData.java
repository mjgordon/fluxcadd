package lisp;

import geometry.Line;
import geometry.Point;

import java.util.ArrayList;

public abstract class LispData {
	
	public LispList parent;
	
	
	public LispData(LispList parent) {
		this.parent = parent;

	}
	
	public abstract boolean receiveChar(char c);
	
	
	public abstract void printIdentity(int depth);
	
	public abstract Object getValue();
		
}