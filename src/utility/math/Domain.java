package utility.math;

public class Domain {
	private double lower;
	private double upper;
	
	public Domain() {
		this.lower = 0;
		this.upper = 1;
	}
	
	public Domain(double lower, double upper) {
		this.lower = lower;
		this.upper = upper;
	}
	
	public double clip(double d) {
		return Math.max(lower, Math.min(upper, d));
	}
	
	public double getNormalize(double d) {
		return( (d - lower) / (upper - lower) );
	}
	
	public double convert(double d, Domain convertFrom) {
		double n = convertFrom.getNormalize(d) * (upper - lower) + lower;
		return(n);
	}
	
	public double getSize() {
		return(upper - lower);
	}
	
	
	
	public double getLower() {
		return(lower);
	}
	
	public void setLower(double lower) {
		this.lower = lower;
	}
	
	public double getUpper() {
		return(upper);
	}
	
	public void setUpper(double upper) {
		this.upper = upper;
	}

}
