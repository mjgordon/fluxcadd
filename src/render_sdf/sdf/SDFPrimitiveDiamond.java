package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;
import org.lwjgl.system.CallbackI.P;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import utility.Color3i;

public class SDFPrimitiveDiamond extends SDF {
	
	private Matrix4dAnimated frame;
	private double axisSize;


	public SDFPrimitiveDiamond(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Diamond");

		this.axisSize = Math.sqrt(Math.pow(size, 2) / 2);
		this.material = material;

		displayName = "PrimDiamond";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		return distanceFunction(v, time, frame.getInvert(time), axisSize);
	}
	
	
	public static double distanceFunction(Vector3d v, double time, Matrix4d mInvert, double axisSize) {
		Vector3d vl = v.mulPosition(mInvert, new Vector3d());
		vl.absolute();
		
		double m = vl.x + vl.y + vl.z - axisSize;
		
		Vector3d q;
		if (3 * vl.x < m) q = new Vector3d(vl.x, vl.y, vl.z);
		else if (3 * vl.x < m) q = new Vector3d(vl.y, vl.z, vl.x);
		else if (3 * vl.x < m) q = new Vector3d(vl.z, vl.x, vl.y);
		else return m * 0.57735027;
		
		double k = Math.max(0, Math.min(axisSize, 0.5 * (q.z - q.y + axisSize)));
		
		return Vector3d.distance(0, 0, 0, q.x, q.y - axisSize + k, q.z - k);	
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		float hp = (float) (axisSize / 2);

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
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		String nameMatrixInvert = "mInvert" + compileName;
		definitions.add("private Matrix4d " + nameMatrixInvert + " = " + getCompileMatrixString(frame.getInvert(time)));
		
		
		String out = "SDFPrimitiveDiamond.distanceFunction(" + vLocalLast + ", " + time + ", " + nameMatrixInvert + ", " + axisSize +  ")";
		return out;
	}

}
