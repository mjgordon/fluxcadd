package render_sdf.sdf;

import render_sdf.material.Material;
import utility.PVectorD;

import org.ejml.data.Complex_F64;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.EigenDecomposition_F64;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.solvers.*;


public class SDFOpFillet extends SDF {
	
	private SDF a;
	private SDF b;
	private double size;
	private double sizeSqrt;
	private double offset;
	
	public SDFOpFillet(SDF a, SDF b, double size) {
		this.a = a;
		this.b = b;
		this.size = size;
		this.sizeSqrt = Math.sqrt(size);
		this.offset = findOffset(size);
		
	
	}

	@Override
	public DistanceData getDistance(PVectorD v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);
		double distA = aD.distance;
		double distB = bD.distance;
		
		double distC = 0;
		
		if (distA > sizeSqrt && distB > sizeSqrt) {
			distC = distA + distB - size;
		}
		else {
			if (distA > distB) {
				distC = calculateC(distA, distB);
			}
			else {
				distC = calculateC(distB, distA);
			}
			
		}
			
		if (Double.isNaN(distC) || Double.isInfinite(distC)) {
			System.out.println("SDF Fillet : Bad Distance Returned");
		}
		
		 
		if (distA <= distB && distA <= distC) {
			return aD;
		}
		else if (distB <= distA && distB <= distC) {
			return bD;
		}
		
		
		else {
			double factor = distA / (distA + distB);
			DistanceData output = new DistanceData(distC,Material.lerpMaterial(aD.material, bD.material, factor));
			return(output);
		}
	}
	
	
	private double calculateC(double distA, double distB) {
		distA += offset;
		distB += offset;
		
		double c4 = 2;
		double c3 = -2 * distA;
		double c2 = 0;
		double c1 = 2 * distB * size;
		double c0 = -2 * size * size;
		
		//double root = findRootED(c0,c1,c2,c3,c4);
		double root = findRootLaguerre(distA, distB, sizeSqrt, (distB > size / distA) ? distA : distA + sizeSqrt, c0,c1,c2,c3,c4);
		
		PVectorD closestPoint = new PVectorD(root,size / root);
		PVectorD virtualAB = new PVectorD(distA, distB);
		
		double distance = PVectorD.dist(virtualAB, closestPoint);
		
		if (distA * distB < size) {
			distance *= -1;
		}
		
		return distance;
	}
	
	
	private static double findOffset(double s) {
		return( (-s + Math.sqrt(Math.pow(s, 2) + (4 * s)))/2);
	}
	
	
	/** 
	 * Return the first root of the polynomial using eigendecomposition. Based on : 
	 * https://ejml.org/wiki/index.php?title=Example_Polynomial_Roots
	 * "While faster techniques do exist for root finding, this is one of the most stable and probably the easiest to implement"
	 * @param coefficients
	 * @return
	 */
	private static double findRootED( double... coefficients ) {
        int N = coefficients.length - 1;

        // Construct the companion matrix
        DMatrixRMaj c = new DMatrixRMaj(N, N);

        double a = coefficients[N];
        for (int i = 0; i < N; i++) {
            c.set(i, N - 1, -coefficients[i]/a);
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
	 * https://stackoverflow.com/a/36285023
	 * Note, efficiency tuning depends highly on choosing suitable min and max values, a better heuristic probably exists
	 * Iterations has not been tuned
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
		double root = laguerreSolver.solve(100,polynomial,min,max);
		return(root);
	}

}
