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

public class SDFPrimitiveStar extends SDF {

	private Matrix4dAnimated frame;
	private double halfSize;
	private double sphereSize;


	public SDFPrimitiveStar(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Star");
		this.halfSize = size / 2;
		this.sphereSize = halfSize * Math.sqrt(2);
		this.material = material;

		displayName = "PrimStar";
	}


	public SDFPrimitiveStar(Matrix4d base, double size, Material material) {
		frame = new Matrix4dAnimated(base, "Star");

		this.halfSize = size / 2;
		this.sphereSize = halfSize * Math.sqrt(2);
		this.material = material;

		displayName = "PrimStar";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vl = v.mulPosition(frame.getInvert(time), new Vector3d()).absolute();

		return distanceFunction(vl, time, halfSize, sphereSize);
	}
	
	
	public static double distanceFunction(Vector3d vl, double time, double halfSize, double sphereSize) {
		double distance;

		// Main curved surface, only closest when within the virtual cube of the shape
		if (vl.x <= halfSize && vl.y <= halfSize && vl.z <= halfSize) {
			distance = sphereSize - Math.sqrt(Math.pow(vl.x - halfSize, 2) + Math.pow(vl.y - halfSize, 2) + Math.pow(vl.z - halfSize, 2));
		}
		// Otherwise, distance is to one of the points
		else if (vl.x >= vl.y && vl.x >= vl.z) {
			distance = Math.sqrt(Math.pow(vl.x - halfSize, 2) + (vl.y * vl.y) + (vl.z * vl.z));
		}
		else if (vl.y >= vl.x && vl.y >= vl.z) {
			distance = Math.sqrt((vl.x * vl.x) + Math.pow(vl.y - halfSize, 2) + (vl.z * vl.z));
		}
		else {
			distance = Math.sqrt((vl.x * vl.x) + (vl.y * vl.y) + Math.pow(vl.z - halfSize, 2));
		}

		return distance;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		Color3i c = getPrimitiveColor(solid, materialPreview);

		g.add(new Line(new Vector3d(-halfSize, 0, 0), new Vector3d(halfSize, 0, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, -halfSize, 0), new Vector3d(0, halfSize, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, 0, -halfSize), new Vector3d(0, 0, halfSize)).setFillColor(c));

		g.setMatrix(frame);

		gd.add(g);
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		Matrix4d matrixInvert = frame.getInvert(time);
		String matrixInvertName = "mInvert" + this.compileName;
		definitions.add("private Matrix4d " + matrixInvertName + " = " + getCompileMatrixString(matrixInvert));
		
		return "SDFPrimitiveStar.distanceFunction(" + vLocalLast + ".mulPosition(" + matrixInvertName + ", new Vector3d()).absolute(), " + time + ", " + halfSize + ", " + sphereSize + " )";
	}

}
