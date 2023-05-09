package render_sdf.animation;

public abstract class Animated {
	protected double[] timeStamps;
	
	protected double cachedTime = Double.NaN;
	
	protected String name = "ANIMATED";
	
	
	
	public double[] getKeyframes() {
		double[] out = new double[timeStamps.length];
		System.arraycopy(timeStamps, 0,out, 0, timeStamps.length);
		return out;
	}
	
	public String getName() {
		return name;
	}
}
