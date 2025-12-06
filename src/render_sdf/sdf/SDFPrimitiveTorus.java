package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import geometry.Sphere;
import geometry.Torus;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;
import utility.Color3i;

public class SDFPrimitiveTorus extends SDFPrimitive {
	private double ringRadius;
	private double profileRadius;


	public SDFPrimitiveTorus(Vector3d position, double ringRadius, double profileRadius, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Torus");
		this.ringRadius = ringRadius;
		this.profileRadius = profileRadius;
		this.material = material;

		displayName = "PrimTorus";
	}


	public SDFPrimitiveTorus(Matrix4d base, double ringRadius, double profileRadius, Material material) {
		frame = new Matrix4dAnimated(base, "Torus");
		this.ringRadius = ringRadius;
		this.profileRadius = profileRadius;
		this.material = material;

		displayName = "PrimTorus";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		return distanceFunction(v, frame.getInvert(time), ringRadius, profileRadius, context);
	}
	
	
	public static double distanceFunction(Vector3d v, Matrix4d frameInvert, double ringRadius, double profileRadius, VectorContext context) {
		Vector3d vl = getVectorLocal(v, frameInvert, context);
		Vector2d vl2d = context.primitiveInternal2d.set(vl.distance(0,0,vl.z), vl.z);
		double distance = vl2d.distance(ringRadius, 0);
		
		return distance - profileRadius;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		gd.add(new Torus(frame, getPrimitiveColor(solid, materialPreview), ringRadius, profileRadius));
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		sourceRepresentationBackground(definitions, time);
		
		return "SDFPrimitiveTorus.distanceFunction(" + vLocalLast + ", " + compileNameMatrixInvert + ", " + ringRadius + ", " + profileRadius + ", context )";
	}
}