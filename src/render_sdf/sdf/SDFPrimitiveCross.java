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

public class SDFPrimitiveCross extends SDF {
	private Matrix4dAnimated frame;

	private double halfSize;
	private double axisSize;

	private double previewSize = 300;


	public SDFPrimitiveCross(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Cross");
		this.halfSize = size / 2;
		this.axisSize = Math.sqrt(Math.pow(size, 2) / 2);
		this.material = material;
		
		displayName = "PrimCross";
	}


	public SDFPrimitiveCross(Matrix4d base, double size, Material material) {
		frame = new Matrix4dAnimated(base, "Cross");		
		this.halfSize = size / 2;
		this.axisSize = Math.sqrt(Math.pow(size, 2) / 2);
		this.material = material;
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vl = new Vector3d(v).mulPosition(frame.getInvert(time));
		vl.absolute();

		if (vl.x <= vl.z && vl.y <= vl.z) {
			return calc2d(vl.x, vl.y);
		}
		else if (vl.x <= vl.y && vl.z <= vl.y) {
			return calc2d(vl.x, vl.z);
		}
		else {
			return calc2d(vl.y, vl.z);
		}
	}


	private double calc2d(double a, double b) {
		// Point is within projected zone
		if (a >= (b - axisSize) && a <= (b + axisSize)) {
			double c = a + b;
			return (Math.sqrt(Math.pow(c, 2) * 0.5) - halfSize);
		}
		// Point is above projected zone
		else if (a < b) {
			return (Math.sqrt(Math.pow(a, 2) + Math.pow(b - axisSize, 2)));	
		}
		// Point is below projected zone
		else {
			return (Math.sqrt(Math.pow(a - axisSize, 2) + Math.pow(b, 2)));
		}	
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		double hp = previewSize / 2;

		Color c = getPrimitiveColor(solid, materialPreview);

		g.add(new Line(new Vector3d(-hp, 0, 0), new Vector3d(hp, 0, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, -hp, 0), new Vector3d(0, hp, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, 0, -hp), new Vector3d(0, 0, hp)).setFillColor(c));

		g.setMatrix(frame);

		gd.add(g);
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] {frame};
	}
	
	public void addKeyframe(double timestamp, Matrix4d m) {
		frame.addKeyframe(timestamp, m);
	}

	

}
