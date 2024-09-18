package utility;

public class MutableInteger extends MutableVariable {
	private int value;
	
	public MutableInteger(int value) {
		this.value = value;
	}
	
	public void set(String value) {
		this.value = Integer.valueOf(value);
	}
	
	public void set(int value) {
		this.value = value;
	}
	
	public int get() {
		return(value);
	}
	
	public String toString() {
		return("" + value);
	}
}
