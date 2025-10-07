package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.Cylinder;
import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

public class SDFPrimitiveCylinder extends SDFPrimitive {
	private double radius;
	private double halfHeight;
	
	private static final Vector2d vectorZero = new Vector2d(0, 0);


	public SDFPrimitiveCylinder(Vector3d position, double radius, double height, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Cylinder");

		this.material = material;

		this.radius = radius;
		this.halfHeight = height * 0.5;

		displayName = "PrimCylinder";
	}


	public SDFPrimitiveCylinder(Matrix4d base, double radius, double height, Material material) {
		frame = new Matrix4dAnimated(base, "Cylinder");

		this.material = material;

		this.radius = radius;
		this.halfHeight = height * 0.5;

		displayName = "PrimCylinder";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		return distanceFunction(v, frame.getInvert(time), radius, halfHeight, context);
	}
	
	
	public static double distanceFunction(Vector3d v, Matrix4d frameInvert, double radius, double halfHeight, VectorContext context) {
		Vector3d vl = getVectorLocal(v, frameInvert, context);
		Vector2d vl2d = context.primitiveInternal2d.set(Math.sqrt(Math.pow(vl.x, 2) + Math.pow(vl.y,  2)), vl.z);
		
		vl2d.absolute().sub(radius, halfHeight);
		
		double max2d = Math.max(vl2d.x, vl2d.y);
		
		return vl2d.max(vectorZero).length() + Math.min(0, max2d);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		gd.add(new Cylinder(frame.get(time).scale(radius, radius, halfHeight), this.getPrimitiveColor(solid, materialPreview)));
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		sourceRepresentationBackground(definitions, time);
		
		return "SDFPrimitiveCylinder.distanceFunction(" + vLocalLast + ", " + compileNameMatrixInvert + ", " + radius + ", " + halfHeight + ", context)";
	}

}
