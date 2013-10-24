package robocam;

public class Vector6 {
	public Float x;
	public Float y;
	public Float z;
	public Float a;
	public Float b;
	public Float c;
	
	public Vector6() {
		
	}
	
	public Vector6(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector6(float x, float y, float z, float a, float b, float c) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public String toString() {
		String pre = "";
		
		String out = "{";
		if (x != null) {
			out += pre + "X " + x;
			pre = ",";
		}
		if (y != null) {
			out += pre + "Y " + y;
			pre = ",";
		}
		if (z != null) {
			out += pre + "Z " + z;
			pre = ",";
		}
		if (a != null) {
			out += pre + "A " + a;
			pre = ",";
		}
		if (b != null) {
			out += pre + "B " + b;
			pre = ",";
		}
		if (c != null) {
			out += pre + "C " + c;
			pre = ",";
		
		}
		out += "}";
		return(out);
	}
	
	public Vector6 setZ(float z) {
		this.z = z;
		return(this);
	}
}
