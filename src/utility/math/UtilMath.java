package utility.math;

public class UtilMath {

	public static final double HALF_PI = Math.PI / 2;
	public static final double TWO_PI = Math.PI * 2;
	public static int clip(int val, int low, int high) {
		return (Math.max(low, Math.min(high, val)));
	}
	public static final int sign(double d) {
		return (int) (d / Math.abs(d));
	}
	public static final double log2(double v) {
		return (Math.log(v) / Math.log(2));
	}
	static public final double lerp(double start, double stop, double amt) {
		return start + ((stop - start) * amt);
	}
	static public final double map(double value, double istart, double istop, double ostart, double ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}

}
