package utility;

/**
 * Orientation data for the robot arm
 * Position and rotation data
 * @author Matt Gordon
 *
 */

public class Vector6 {
	public double x = 0;
	public double y = 0;
	public double z = 0;
	public double a = 0;
	public double b = 0;
	public double c = 0;
	
	public Vector6() {
		
	}
	
	public Vector6(PVector xyz, PVector abc) {
		this.x = xyz.x;
		this.y = xyz.y;
		this.z = xyz.z;
		this.a = abc.x;
		this.b = abc.y;
		this.c = abc.z;
	}
	
	public Vector6(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector6(double x, double y, double z, double a, double b, double c) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public PVector getXYZ() {
		return(new PVector(x,y,z));
	}
	
	public PVector getABC() {
		return(new PVector(a,b,c));
	}
	
	public String toString() {
		String out = "{";
		out += "X " + x;
		out += ",Y " + y;
		out += ",Z " + y;
		out += ",A " + y;
		out += ",B " + y;
		out += ",C " + y;
		out += "}";
		return(out);
	}
	
	public Vector6 setZ(float z) {
		this.z = z;
		return(this);
	}
}
