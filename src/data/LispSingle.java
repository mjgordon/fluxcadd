package data;

public class LispSingle extends LispData {
	
	String data;
	
	public LispSingle(LispData parent) {
		this.parent = parent;
		data = "";
	}
	
	@Override
	public void receiveChar(char c) {
		
	}


}
