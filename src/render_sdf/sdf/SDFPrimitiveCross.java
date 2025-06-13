package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix3x2d;
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

public class SDFPrimitiveCross extends SDF {

	private Matrix4dAnimated frame;
	
	private Matrix3x2d matrixInvert2d;

	private double axisSize;
	private double hypotSize;

	private double previewSize = 300;


	public SDFPrimitiveCross(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Cross");
		this.axisSize = size / 2;
		this.hypotSize = Math.sqrt(Math.pow(axisSize, 2) * 2);
		this.material = material;

		displayName = "PrimCross";
		
		setupMatrix();
	}


	public SDFPrimitiveCross(Matrix4d base, double size, Material material) {
		frame = new Matrix4dAnimated(base, "Cross");
		this.axisSize = size / 2;
		this.hypotSize = Math.sqrt(Math.pow(axisSize, 2) * 2);
		this.material = material;
		
		setupMatrix();
	}
	
	private void setupMatrix() {
		Matrix3x2d m = new Matrix3x2d();
		double n = Math.sqrt(2) * 0.5;
		m.set(n, -n, n, n, 0, axisSize);
		m.invert();
		this.matrixInvert2d = m;
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		return distanceFunction(v, time, frame.getInvert(time), matrixInvert2d, hypotSize);
	}
	
	
	public static double distanceFunction(Vector3d v, double time, Matrix4d matrixInvert, Matrix3x2d matrixInvert2d, double hypotSize) {
		Vector3d vl = new Vector3d(v).mulPosition(matrixInvert);
		vl.absolute();

		Vector2d pos;
		if (vl.x <= vl.z && vl.y <= vl.z) {
			pos = new Vector2d(vl.x, vl.y);
		}
		else if (vl.x <= vl.y && vl.z <= vl.y) {
			pos = new Vector2d(vl.x, vl.z);
		}
		else {
			pos = new Vector2d(vl.y, vl.z);
		}
		
		pos.mulPosition(matrixInvert2d);
		
		Vector2d comp = new Vector2d(Math.min(hypotSize, Math.max(0, pos.x) ), 0);
		
		double dist = pos.distance(comp);
		
		dist *= Math.signum(pos.y);
		
		return dist;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		double hp = previewSize / 2;

		Color3i c = getPrimitiveColor(solid, materialPreview);

		g.add(new Line(new Vector3d(-hp, 0, 0), new Vector3d(hp, 0, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, -hp, 0), new Vector3d(0, hp, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, 0, -hp), new Vector3d(0, 0, hp)).setFillColor(c));

		g.setMatrix(frame);

		gd.add(g);
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}


	public void addKeyframe(double timestamp, Matrix4d m) {
		frame.addKeyframe(timestamp, m);
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		String nameMatrixInvert = "mInvert" + compileName;
		definitions.add("private Matrix4d " + nameMatrixInvert + " = " + getCompileMatrixString(frame.getInvert(time)));
		
		String nameMatrixInvert2d = "mInvert2d" + compileName;
		definitions.add("private Matrix3x2d " + nameMatrixInvert2d + " = " + getCompileMatrixString3x2(matrixInvert2d));
		
		
		String out = "SDFPrimitiveCross.distanceFunction(" + vLocalLast + ", " + time + ", " + nameMatrixInvert + ", " + nameMatrixInvert2d + ", " + hypotSize + ")";
		return out;
	}
	

}
