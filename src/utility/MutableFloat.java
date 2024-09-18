package utility;

public class MutableFloat extends MutableVariable {
	private float value;


	public MutableFloat(float value) {
		this.value = value;
	}


	public void set(float value) {
		this.value = value;
	}


	public float get() {
		return (value);
	}


	@Override
	public void set(String value) {
		this.value = Float.valueOf(value);
	}


	public String toString() {
		return ("" + value);
	}
}
