package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;

public class SDFOpModulo extends SDF {
	
	private SDF child;
	
	private double stride;
	
	
	
	public SDFOpModulo(SDF child, double stride) {
		this.child = child;
		this.stride = stride;
	}

	@Override
	public DistanceData getDistance(Vector3d v) {
		Vector3d copyA = new Vector3d(v);
		
		copyA.x %= stride;
		copyA.y %= stride;
		
		Vector3d copyB = new Vector3d(copyA);
		Vector3d copyC = new Vector3d(copyA);
		Vector3d copyD = new Vector3d(copyA);
		
		
		if (copyB.x < stride && copyB.x > 0) {
			copyB.x -= stride;
		}
		
		else if (copyB.x > -stride && copyB.x < 0) {
			copyB.x += stride;
		}
		
		if (copyC.y < stride && copyC.y > 0) {
			copyC.y -= stride;
		}
		
		else if (copyC.y > -stride && copyC.y < 0) {
			copyC.y += stride;
		}
		
		if (copyD.x < stride && copyD.x > 0) {
			copyD.x -= stride;
		}
		else if (copyD.x > -stride && copyD.x < 0) {
			copyD.x += stride;
		}
		
		if (copyD.y < stride && copyD.y > 0) {
			copyD.y -= stride;
		}
		else if (copyD.y > -stride && copyD.y < 0) {
			copyD.y += stride;
		}
		
		DistanceData distO = child.getDistance(v);
		DistanceData distA = child.getDistance(copyA);
		DistanceData distB = child.getDistance(copyB);
		DistanceData distC = child.getDistance(copyC);
		DistanceData distD = child.getDistance(copyD);
		
		if (distA.distance < distO.distance) {
			distO.distance = distA.distance;
		}
		
		if (distB.distance < distO.distance) {
			distO.distance = distB.distance;
		}
		
		if (distC.distance < distO.distance) {
			distO.distance = distC.distance;
		}
		
		if (distD.distance < distO.distance) {
			distO.distance = distD.distance;
		}
		
		return(distO);
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		child.extractSceneGeometry(gd, solid);
	}

}
