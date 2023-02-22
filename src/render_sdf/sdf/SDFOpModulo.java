package render_sdf.sdf;

import org.joml.Vector3d;

import geometry.GeometryDatabase;

public class SDFOpModulo extends SDF {

	private SDF child;

	private double strideX = -1;
	private double strideY = -1;
	private double strideZ = -1;


	public SDFOpModulo(SDF child, double stride) {
		this.child = child;
		this.strideX = stride;
		this.strideY = stride;
		this.strideZ = stride;
	}


	public SDFOpModulo(SDF child, double strideX, double strideY, double strideZ) {
		this.child = child;
		this.strideX = strideX;
		this.strideY = strideY;
		this.strideZ = strideZ;
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
		
		datas[0] = child.getDistance(copyA, time);
		
		int n = 1;
		
		for (int i = 1; i < 8; i++) {
			boolean bx2 = (i & 1) == 1;
			boolean by2 = (i & 2) == 2;
			boolean bz2 = (i & 4) == 4;
			
			datas[i] = child.getDistance(modVector(new Vector3d(copyA), bx2, by2, bz2), time);
		}
		


		DistanceData distO = child.getDistance(v, time);

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
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		child.extractSceneGeometry(gd, solid, materialPreview);
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
	public String describeTree(String input, int depth, String spacer) {
		input = super.describeTree(input, depth, spacer);
		input += "OpModulo";
		input = child.describeTree(input, depth + 1, PIPE_ELBOW);
		return input;
	}

}
