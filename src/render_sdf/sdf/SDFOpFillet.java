package render_sdf.sdf;


import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.*;
import org.ejml.data.Complex_F64;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.EigenDecomposition_F64;
import org.joml.Vector2d;
import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

/**
 * Currently superceded by the properly efficient SDFOpSmooth version. Left
 * together as the curved surface methods may be useful elsewhere in the future
 *
 */
public class SDFOpFillet extends SDF {
	private double size;
	private double sizeSqrt;
	private double offset;

	private static double[] heuristicX;
	private static double[] heuristicA;
	private static double[] heuristicC;


	public SDFOpFillet(SDF a, SDF b, double size) {
		this.childA = a;
		this.childB = b;
		this.size = size;
		this.sizeSqrt = Math.sqrt(size);
		this.offset = findOffset(size);

		double x1 = 0.1;
		double x2 = 0.2;
		heuristicX = new double[15];
		heuristicA = new double[heuristicX.length - 1];
		heuristicC = new double[heuristicX.length - 1];

		heuristicX[0] = SDF.epsilon;
		for (int i = 1; i < heuristicX.length; i++) {
			heuristicX[i] = x1;
			double temp = x1 + x2;
			x1 = x2;
			x2 = temp;
		}

		for (int i = 0; i < heuristicA.length; i++) {
			double y0 = (size / heuristicX[i]);
			double y1 = (size / heuristicX[i + 1]);
			heuristicA[i] = (y1 - y0) / (heuristicX[i + 1] - heuristicX[i]);
			heuristicC[i] = y0 - (heuristicA[i] * heuristicX[i]);
		}
		
		displayName = "OpFillet";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);

		double cd = 0;

		if (ad > bd) {
			cd = getDistanceHeuristic(ad, bd);
			if (cd < 0) {
				cd = calculateC(ad, bd);
			}
		}
		else {
			cd = getDistanceHeuristic(bd, ad);
			if (cd < 0) {
				cd = calculateC(bd, ad);
			}
		}

		if (ad <= bd && ad <= cd) {
			return ad;
		}
		else if (bd <= ad && bd <= cd) {
			return bd;
		}
		else {
			return cd;
		}
	}
	
	
	@Override
	public Material getMaterial(Vector3d v, double time) {
		double ad = childA.getDistance(v, time);
		double bd = childB.getDistance(v, time);

		double cd = 0;

		if (ad > bd) {
			cd = getDistanceHeuristic(ad, bd);
			if (cd < 0) {
				cd = calculateC(ad, bd);
			}
		}
		else {
			cd = getDistanceHeuristic(bd, ad);
			if (cd < 0) {
				cd = calculateC(bd, ad);
			}
		}

		if (ad <= bd && ad <= cd) {
			return childA.getMaterial(v, time);
		}
		else if (bd <= ad && bd <= cd) {
			return childB.getMaterial(v, time);
		}
		else {
			double factor = ad / (ad + bd);
			return Material.lerpMaterial(childA.getMaterial(v, time), childB.getMaterial(v, time), factor);
		}
	}
	

	private double getDistanceHeuristic(double da, double db) {
		double bestDistance = Double.MAX_VALUE;

		for (int i = 0; i < heuristicA.length; i++) {

			double a = heuristicA[i];
			double c = heuristicC[i];

			double lx = ((da - (a * db)) - (a * c)) / (Math.pow(a, 2) + 1);
			double ly = ((-da + (a * db)) - (c)) / (Math.pow(a, 2) + 1);

			double distance = Math.sqrt(Math.pow(da - lx, 2) + Math.pow(db - ly, 2));

			if (distance < bestDistance) {
				bestDistance = distance;
			}
			else {
				break;
			}
		}

		// bestDistance as calculated is only accurate when 'outside' the heuristic
		// Dummy 'inside' value is returned here as it will be replaced by the real
		// distance
		if (db < size / da) {
			bestDistance = -1;
		}

		return (bestDistance - SDF.epsilon);
	}


	private double calculateC(double distA, double distB) {
		distA += offset;
		distB += offset;

		double c4 = 2;
		double c3 = -2 * distA;
		double c2 = 0;
		double c1 = 2 * distB * size;
		double c0 = -2 * size * size;

		// double root = findRootED(c0,c1,c2,c3,c4);
		double root = findRootLaguerre(distA, distB, sizeSqrt, (distB > size / distA) ? distA : distA + sizeSqrt, c0, c1, c2, c3, c4);

		double distance = Vector2d.distance(root, size / root, distA, distB);

		if (distA * distB < size) {
			distance *= -1;
		}

		return distance;
	}


	/**
	 * Calculate the offset to approximate the a curve that intersects the axes at
	 * the same positions as (x * y) + (x + y) = 3 Calculates the quadratic root for
	 * the equation y = (s / (x + n)) - n, where x = s and y = 0
	 * 
	 * @param s
	 * @return
	 */
	private static double findOffset(double s) {
		return ((-s + Math.sqrt(Math.pow(s, 2) + (4 * s))) / 2);
	}


	/**
	 * Return the first root of the polynomial using eigendecomposition. Based on :
	 * https://ejml.org/wiki/index.php?title=Example_Polynomial_Roots "While faster
	 * techniques do exist for root finding, this is one of the most stable and
	 * probably the easiest to implement"
	 * 
	 * @param coefficients
	 * @return
	 */
	@SuppressWarnings("unused")
	private static double findRootED(double... coefficients) {
		int N = coefficients.length - 1;

		// Construct the companion matrix
		DMatrixRMaj c = new DMatrixRMaj(N, N);

		double a = coefficients[N];
		for (int i = 0; i < N; i++) {
			c.set(i, N - 1, -coefficients[i] / a);
		}
		for (int i = 1; i < N; i++) {
			c.set(i, i - 1, 1);
		}

		// use generalized eigenvalue decomposition to find the roots
		EigenDecomposition_F64<DMatrixRMaj> evd = DecompositionFactory_DDRM.eig(N, false);

		evd.decompose(c);

		Complex_F64[] roots = new Complex_F64[N];

		for (int i = 0; i < N; i++) {
			roots[i] = evd.getEigenvalue(i);
		}

		return roots[0].real;
	}


	/**
	 * Return the first root of the polynomial using the Laguerre Solver. Based on:
	 * https://stackoverflow.com/a/36285023 Note, efficiency tuning depends highly
	 * on choosing suitable min and max values, a better heuristic probably exists
	 * Iterations has not been tuned
	 * 
	 * @param distA
	 * @param distB
	 * @param min
	 * @param max
	 * @param coefficients
	 * @return
	 */
	private static double findRootLaguerre(double distA, double distB, double min, double max, double... coefficients) {
		PolynomialFunction polynomial = new PolynomialFunction(coefficients);
		LaguerreSolver laguerreSolver = new LaguerreSolver();
		double root = laguerreSolver.solve(100, polynomial, min, max);
		return (root);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
		childB.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		return null;
	}


}
