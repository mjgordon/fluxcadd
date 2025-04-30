package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import utility.Color3i;

public class SDFPrimitiveCube extends SDF {
	private Matrix4dAnimated frame;

	private Vector3d dimensions;

	private Vector3d helper = new Vector3d();


	public SDFPrimitiveCube(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		dimensions = new Vector3d(size / 2, size / 2, size / 2);

		this.frame = new Matrix4dAnimated(base, "Cube");

		this.material = material;

		displayName = "PrimCube";
	}


	public SDFPrimitiveCube(Vector3d position, double sizeX, double sizeY, double sizeZ, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		
		dimensions = new Vector3d(sizeX / 2, sizeY / 2, sizeZ / 2);

		this.frame = new Matrix4dAnimated(base, "Cube");

		this.material = material;

		displayName = "PrimCube";
	}


	public void addKeyframe(double timestamp, Matrix4d m) {
		frame.addKeyframe(timestamp, m);
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Matrix4d frameInvert = frame.getInvert(time);

		Vector3d vLocal = frameInvert.transformPosition(v, helper);
		
		Vector3d q = vLocal.absolute().sub(dimensions);
		
		double maxQ = Math.max(q.x, Math.max(q.y, q.z));
		
		Vector3d zero = new Vector3d(0, 0, 0);
		
		double distance = q.max(zero, new Vector3d()).length() + Math.min(0, maxQ);

		return distance;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		Color3i c = getPrimitiveColor(solid, materialPreview);

		double x = dimensions.x;
		double y = dimensions.y;
		double z = dimensions.z;

		g.add(new Line(new Vector3d(-x, -y, -z), new Vector3d(x, -y, -z)).setFillColor(c));
		g.add(new Line(new Vector3d(-x, y, -z), new Vector3d(x, y, -z)).setFillColor(c));
		g.add(new Line(new Vector3d(-x, -y, z), new Vector3d(x, -y, z)).setFillColor(c));
		g.add(new Line(new Vector3d(-x, y, z), new Vector3d(x, y, z)).setFillColor(c));

		g.add(new Line(new Vector3d(-x, -y, -z), new Vector3d(-x, y, -z)).setFillColor(c));
		g.add(new Line(new Vector3d(x, -y, -z), new Vector3d(x, y, -z)).setFillColor(c));
		g.add(new Line(new Vector3d(-x, -y, z), new Vector3d(-x, y, z)).setFillColor(c));
		g.add(new Line(new Vector3d(x, -y, z), new Vector3d(x, y, z)).setFillColor(c));

		g.add(new Line(new Vector3d(-x, -y, -z), new Vector3d(-x, -y, z)).setFillColor(c));
		g.add(new Line(new Vector3d(x, -y, -z), new Vector3d(x, -y, z)).setFillColor(c));
		g.add(new Line(new Vector3d(-x, y, -z), new Vector3d(-x, y, z)).setFillColor(c));
		g.add(new Line(new Vector3d(x, y, -z), new Vector3d(x, y, z)).setFillColor(c));

		g.setMatrix(frame);

		gd.add(g);
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	/*
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> prelines, String vLocalLast, double time) {
		Matrix4d matrixInvert = frame.getInvert(time);
		String matrixInvertName = "mInvert" + this.compileName;
		definitions.add("Matrix4d " + matrixInvertName + " = " + getCompileMatrixString(matrixInvert));
		
		String output = vLocalLast + ".mulPosition(" + matrixInvertName + ", new Vector3d()).length() - " + radius;
		return output;
	}*/
}
