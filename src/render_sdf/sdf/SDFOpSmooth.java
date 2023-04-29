package render_sdf.sdf;


import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;

/**
 * Based on the polynomial smooth method described by Inigo Quilez at:
 * https://iquilezles.org/articles/smin/
 * 
 * @author mattj
 *
 */
public class SDFOpSmooth extends SDF {
	private double size;


	public SDFOpSmooth(SDF a, SDF b, double size) {
		this.childA = a;
		this.childB = b;
		this.size = size;
		
		displayName = "OpSmooth";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		DistanceData aD = childA.getDistance(v, time);
		DistanceData bD = childB.getDistance(v, time);
		double distA = aD.distance;
		double distB = bD.distance;
		
		/* Optimization attempt, produces some weird artifacts currently
		if (distA > size || distB > size) {
			if (distA < distB) {
				return aD;
			}
			else {
				return bD;
			}
		}
		*/

		double h = Math.max(size - Math.abs(distA - distB), 0.0) / size;
		double distC = Math.min(distA, distB) - h * h * size * 0.25;

		if (distA <= distB && distA <= distC) {
			return aD;
		}
		else if (distB <= distA && distB <= distC) {
			return bD;
		}
		else {
			double factor = distA / (distA + distB);
			DistanceData output = new DistanceData(distC, Material.lerpMaterial(aD.material, bD.material, factor));
			return (output);
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
		childB.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		// TODO Auto-generated method stub
		return null;
	}


}
