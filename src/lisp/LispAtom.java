package lisp;

public class LispAtom extends LispData {

	public String data = "";
	
	public LispAtom(LispList parent) {
		super(parent);
	}
	
	public boolean receiveChar(char c) {
		data += c;
		return(false);
	}
	
	public void printIdentity(int depth) {
		String out = "";
		for (int i = 0; i < depth; i++) out += ".";
		out += data;
		System.out.println(out);
	}
	
	public Object getValue() {
		return data;
	}
	
	public String asString() {
		return(data);
	}
	
	public float asFloat() {
		return(Float.valueOf(data));
	}
}
