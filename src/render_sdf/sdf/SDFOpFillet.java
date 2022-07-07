package render_sdf.sdf;

import render_sdf.material.Material;
import utility.Color;
import utility.PVectorD;
import utility.Util;

import org.ejml.*;
import org.ejml.data.Complex_F64;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.EigenDecomposition_F64;

public class SDFOpFillet extends SDF {
	
	private SDF a;
	private SDF b;
	private double size;
	
	public SDFOpFillet(SDF a, SDF b, double size) {
		this.a = a;
		this.b = b;
		this.size = size;
		
		//System.out.println(calculateC(new PVectorD(2.3,8.5)));
	}

	@Override
	public DistanceData getDistance(PVectorD v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);
		double distA = aD.distance;
		double distB = bD.distance;
		
		
		//double distC = (Math.abs(distA) * Math.abs(distB)) +  Math.abs(distA) + Math.abs(distB) - size;
		//distC = (distA * distB) + (distA + distB) - size;
		
		double distC = calculateC(distA, distB);
		
		if (Double.isNaN(distC) || Double.isInfinite(distC)) {
			System.out.println("shit");
		}
		
		 
		if (distA <= distB && distA <= distC) {
			return aD;
		}
		else if (distB <= distA && distB <= distC) {
			return bD;
		}
		
		
		else {
			double da = Math.abs(distA - distC);
			double db = Math.abs(distB - distC);
			//double dab = Math.abs(distA - distB);
			//dab = Math.abs(distA + distB);
			double dab = Math.abs(distA) + Math.abs(distB);
			double factor = Math.abs(da) /dab ;
			
			factor = distA / (distA + distB);
			/*// TODO: This is still happening
			if (factor < 0 || factor > 1) {
				System.out.println("=============");
				System.out.println("F: " + factor);
				System.out.println("A: " + distA);
				System.out.println("B: " + distB);
				System.out.println("C: " + distC);
			}
			*/
			DistanceData output = new DistanceData(distC,Material.lerpMaterial(aD.material, bD.material, factor));
			//output = new DistanceData(distC,new Material(new Color(0xFF00FF),0));
			return(output);
		}
		
		
	}
	
	
	public double calculateC(double distA, double distB) {
		distA += 0.86;
		distB += 0.86;
		
		double c4 = 2;
		double c3 = -2 * distA;
		double c2 = 0;
		double c1 = 2 * distB;
		double c0 = -2;
		
		Complex_F64[] roots = findRoots(c0,c1,c2,c3,c4);
		
		/*
		for (int i = 0; i < roots.length; i++) {
			System.out.println(i + " : " + roots[i]);
		}
		*/
		
		PVectorD closestPoint = new PVectorD(roots[0].real,size / roots[0].real);
		PVectorD virtualAB = new PVectorD(distA, distB);
		
		double distance = PVectorD.dist(virtualAB, closestPoint);
		
		if (distA * distB < size) {
			distance *= -1;
		}
		
		return distance;
	}
	
	
	public static Complex_F64[] findRoots( double... coefficients ) {
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

        return roots;
    }

}
