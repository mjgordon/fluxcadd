package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveDiamond extends SDF {
	private Matrix4dAnimated frame;

	private double halfSize;
	
	private double axisSize;


	public SDFPrimitiveDiamond(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position,1));
		frame = new Matrix4dAnimated(base, "Diamond");
		
		this.halfSize = size / 2;
		this.axisSize = Math.sqrt(Math.pow(size, 2) / 2);
		this.material = material;
		
		displayName = "PrimDiamond";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vl = v.mulPosition(frame.getInvert(time),new Vector3d());
		vl.absolute();
		
		double sum = vl.x + vl.y + vl.z;
		double offset = (sum - axisSize) / 3;
		
		double faceX = vl.x - offset;
		double faceY = vl.y - offset;
		double faceZ = vl.z - offset;
		
		boolean bx = faceX >= 0;
		boolean by = faceY >= 0;
		boolean bz = faceZ >= 0;
		
		
		if (!bx && !by) {
			return vl.distance(0, 0, axisSize);
		}
		else if (!by && !bz) {
			return vl.distance(axisSize, 0, 0);
		}
		else if (!bz && !bx) {
			return vl.distance(0, axisSize, 0);
		}
		else if (!bx) {
			double yzSum = vl.y + vl.z - axisSize;
			double yzOffset = yzSum / 2;
			return vl.distance(0, vl.y - yzOffset, vl.z - yzOffset);
		}
		else if (!by) {
			double xzSum = vl.x + vl.z - axisSize;
			double xzOffset = xzSum / 2;
			return vl.distance(vl.x - xzOffset, 0, vl.z - xzOffset);
		}
		else if (!bz) {
			double xySum = vl.x + vl.y - axisSize;
			double xyOffset = xySum / 2;
			return vl.distance(vl.x - xyOffset,  vl.y - xyOffset, 0);
		}
		// Point is within the projected face
		else if (bx && by && bz) {
			return vl.distance(faceX, faceY, faceZ);
		}
		else {
			System.out.println("this shouldn't happen");
		}
		
		return Double.NaN;
	}
	
	
	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();
		
		float hp = (float) (axisSize / 2);
		
		Color c = getPrimitiveColor(solid, materialPreview);
		
		g.add(new Line(new Vector3d(-hp,0,0), new Vector3d(hp,0,0)).setFillColor(c));
		g.add(new Line(new Vector3d(0,-hp,0), new Vector3d(0,hp,0)).setFillColor(c));
		g.add(new Line(new Vector3d(0,0,-hp), new Vector3d(0,0,hp)).setFillColor(c));
		
		g.setMatrix(frame);
		
		gd.add(g);
	}
	
	
	@Override
	public Animated[] getAnimated() {
		return new Animated[] {frame};
	}

}
