package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import utility.Color3i;

public class SDFPrimitiveCylinder extends SDF {

	private Matrix4dAnimated frame;
	private double radius;
	private double halfHeight;


	public SDFPrimitiveCylinder(Vector3d position, double radius, double height, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		base.scale(radius, radius, height / 2);
		frame = new Matrix4dAnimated(base, "Cylinder");

		this.material = material;

		this.radius = radius;
		this.halfHeight = height * 0.5;

		displayName = "PrimCylinder";
	}


	public SDFPrimitiveCylinder(Matrix4d base, double radius, double height, Material material) {
		base.scale(radius, radius, height / 2);
		frame = new Matrix4dAnimated(base, "Cylinder");

		this.material = material;

		this.radius = radius;
		this.halfHeight = height * 0.5;

		displayName = "PrimCylinder";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		return distanceFunction(v, time, frame.getInvertNormal(time), radius, halfHeight);
	}
	
	
	public static double distanceFunction(Vector3d v, double time, Matrix4d frameInvert, double radius, double halfHeight) {
		Vector3d vl = v.mulPosition(frameInvert, new Vector3d());
		Vector2d local = new Vector2d(Math.sqrt(Math.pow(vl.x, 2) + Math.pow(vl.y,  2)), vl.z);
		Vector2d sub = new Vector2d(radius, halfHeight);
		Vector2d zero = new Vector2d(0, 0);
		
		local.absolute().sub(sub);
		
		return local.max(zero, new Vector2d()).length() + Math.min(0, Math.max(local.x, local.y));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		Color3i color = getPrimitiveColor(solid, materialPreview);

		int count = 16;

		for (int i = 0; i < count; i++) {
			double n = Math.PI * 2 * i / count;
			double n2 = Math.PI * 2 * (i + 1) / count;

			double x = Math.cos(n);
			double y = Math.sin(n);
			double x2 = Math.cos(n2);
			double y2 = Math.sin(n2);

			g.add(new Line(new Vector3d(x, y, 1), new Vector3d(x, y, -1)).setFillColor(color));

			g.add(new Line(new Vector3d(x, y, 1), new Vector3d(0, 0, 1)).setFillColor(color));
			g.add(new Line(new Vector3d(x, y, -1), new Vector3d(0, 0, -1)).setFillColor(color));

			g.add(new Line(new Vector3d(x, y, 1), new Vector3d(x2, y2, 1)).setFillColor(color));
			g.add(new Line(new Vector3d(x, y, -1), new Vector3d(x2, y2, -1)).setFillColor(color));
		}

		g.setMatrix(frame);

		gd.add(g);

	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		Matrix4d matrixInvert = frame.getInvertNormal(time);
		String matrixInvertName = "mInvert" + this.compileName;
		definitions.add("private Matrix4d " + matrixInvertName + " = " + getCompileMatrixString(matrixInvert));
		
		return "SDFPrimitiveCylinder.distanceFunction(" + vLocalLast + ", " + time + ", " + matrixInvertName + ", " + radius + ", " + halfHeight + ")";
	}

}
