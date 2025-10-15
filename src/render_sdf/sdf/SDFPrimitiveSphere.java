package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Polyline;
import geometry.Sphere;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

public class SDFPrimitiveSphere extends SDFPrimitive {
	private double radius;


	public SDFPrimitiveSphere(Vector3d position, double radius, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Sphere");
		this.radius = radius;
		this.material = material;

		displayName = "PrimSphere";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		Vector3d vl = getVectorLocal(v, frame.getInvert(time), context);
		
		return vl.length() - radius;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		gd.add(new Sphere(frame, getPrimitiveColor(solid, materialPreview), radius));
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		sourceRepresentationBackground(definitions, time);
		
		return "(SDFPrimitive.getVectorLocal(" + vLocalLast + ", " + compileNameMatrixInvert + ", context).length() - " + radius + ")";
	}

}
