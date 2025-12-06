package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.Axes;
import geometry.GeometryDatabase;
import geometry.Octahedron;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

public class SDFPrimitiveStar extends SDFPrimitive {
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
	public double getDistance(Vector3d v, double time, VectorContext context) {
		return distanceFunction(v, frame.getInvert(time), halfSize, sphereSize, context);
	}
	
	
	public static double distanceFunction(Vector3d v, Matrix4d frameInvert, double halfSize, double sphereSize, VectorContext context) {
		Vector3d vl = getVectorLocal(v, frameInvert, context).absolute();
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
		gd.add(new Axes(frame, getPrimitiveColor(solid, materialPreview), (float)halfSize / 10));
		gd.add(new Octahedron(frame.get(time), getPrimitiveColor(solid, materialPreview), (float)halfSize));
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		sourceRepresentationBackground(definitions, time);
		
		return "SDFPrimitiveStar.distanceFunction(" + vLocalLast + ", " + compileNameMatrixInvert + ", " + halfSize + ", " + sphereSize + ", context)";
	}

}
