package render_sdf.sdf;


import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;

public class SDFOpModulo extends SDF {

	private SDF a;

	private double strideX = -1;
	private double strideY = -1;
	private double strideZ = -1;


	public SDFOpModulo(SDF child, double stride) {
		this.a = child;
		this.strideX = stride;
		this.strideY = stride;
		this.strideZ = stride;
		
		displayName = "OpModulo";
	}


	public SDFOpModulo(SDF child, double strideX, double strideY, double strideZ) {
		this.a = child;
		this.strideX = strideX;
		this.strideY = strideY;
		this.strideZ = strideZ;
		
		displayName = "OpModulo";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		Vector3d copyA = new Vector3d(v);
		
		boolean bx = strideX > 0;
		boolean by = strideY > 0;
		boolean bz = strideZ > 0;
		
		int copies = 1;

		if (bx) {
			copyA.x %= strideX;
			copies *= 2;
		}
		if (by) {
			copyA.y %= strideY;
			copies *= 2;
		}
		if (bz) {
			copyA.z %= strideZ;
			copies *= 2;
			
		}
		
		
		DistanceData[] datas = new DistanceData[8];
		
		datas[0] = a.getDistance(copyA, time);
		
		int n = 1;
		
		for (int i = 1; i < 8; i++) {
			boolean bx2 = (i & 1) == 1;
			boolean by2 = (i & 2) == 2;
			boolean bz2 = (i & 4) == 4;
			
			datas[i] = a.getDistance(modVector(new Vector3d(copyA), bx2, by2, bz2), time);
		}
		


		DistanceData distO = a.getDistance(v, time);

		//System.out.println("yo");
		//System.out.println(distO + " : " + distO.distance);
		for (DistanceData dd : datas) {
			//System.out.println(dd + " : " + dd.distance);
			if (dd.distance < distO.distance) {
				distO.distance = dd.distance;
			}
		}

		return (distO);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		a.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	private Vector3d modVector(Vector3d v, boolean x, boolean y, boolean z) {
		if (x) {
			if (v.x < strideX && v.x > 0) {
				v.x -= strideX;
			}

			else if (v.x > -strideX && v.x < 0) {
				v.x += strideX;
			}
		}

		if (y) {
			if (v.y < strideY && v.y > 0) {
				v.y -= strideY;
			}

			else if (v.y > -strideY && v.y < 0) {
				v.y += strideY;
			}
		}

		if (z) {
			if (v.z < strideZ && v.z > 0) {
				v.z -= strideZ;
			}

			else if (v.z > -strideZ && v.z < 0) {
				v.z += strideZ;
			}
		}

		return v;
	}


	@Override
	public Animated[] getAnimated() {
		// TODO Auto-generated method stub
		return null;
	}


	

}
