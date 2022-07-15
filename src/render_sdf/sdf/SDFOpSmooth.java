package render_sdf.sdf;

import render_sdf.material.Material;
import utility.PVectorD;

/**
 * Based on the polynomial smooth method described by Inigo Quilez at:
 * https://iquilezles.org/articles/smin/
 * @author mattj
 *
 */
public class SDFOpSmooth extends SDF {
	
	private SDF a;
	private SDF b;
	private double size;

	
	
	public SDFOpSmooth(SDF a, SDF b, double size) {
		this.a = a;
		this.b = b;
		this.size = size;
	}

	
	@Override
	public DistanceData getDistance(PVectorD v) {
		DistanceData aD = a.getDistance(v);
		DistanceData bD = b.getDistance(v);
		double distA = aD.distance;
		double distB = bD.distance;
		
		double h = Math.max(size - Math.abs(distA-distB),0.0) / size;
		double distC = Math.min(distA, distB) - h * h * size * (1.0 / 4.0);
		
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

}
