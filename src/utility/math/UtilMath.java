package utility.math;

public class UtilMath {

	public static final double HALF_PI = Math.PI / 2;

	public static final double TWO_PI = Math.PI * 2;


	public static int clip(int val, int low, int high) {
		return (Math.max(low, Math.min(high, val)));
	}


	/**
	 * Returns a signed unit value
	 * @param d
	 * @return 1 if positive, -1 if negative
	 */
	public static final int sign(double d) {
		return (int) (d / Math.abs(d));
	}


	/**
	 * Log base 2
	 * @param v
	 * @return
	 */
	public static final double log2(double v) {
		return (Math.log(v) / Math.log(2));
	}


	/**
	 * Linear interpolation into one domain
	 * @param start
	 * @param stop
	 * @param factor
	 * @return
	 */
	static public final double lerp(double start, double stop, double factor) {
		return start + ((stop - start) * factor);
	}


	/**
	 * Linear interpolation between two domains
	 * @param value
	 * @param istart input domain
	 * @param istop  input domain
	 * @param ostart output domain
	 * @param ostop  output domain
	 * @return
	 */
	static public final double map(double value, double istart, double istop, double ostart, double ostop) {
		return ostart + (ostop - ostart) * ((value - istart) / (istop - istart));
	}
}
