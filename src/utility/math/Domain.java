package utility.math;

public class Domain {
	private float lower;
	private float upper;
	
	public Domain() {
		this.lower = 0;
		this.upper = 1;
	}
	
	public Domain(float lower, float upper) {
		this.lower = lower;
		this.upper = upper;
	}
	
	public float clip(float f) {
		return Math.max(lower, Math.min(upper, f));
	}
	
	public float getNormalize(float f) {
		return( (f - lower) / (upper - lower) );
	}
	
	public float convert(float f, Domain d) {
		float n = d.getNormalize(f) * (upper - lower) + lower;
		return(n);
	}

}
