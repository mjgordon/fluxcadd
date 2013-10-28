package utility;

public class MutableFloat {
	public float value;
	
	public MutableFloat(float value) {
		this.value = value;
	}
	
	public void set(float value) {
		this.value = value;
	}
	
	public float get() {
		return(value);
	}
}
