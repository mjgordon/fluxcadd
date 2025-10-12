package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import geometry.Plane;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;
import utility.Color3i;

public class SDFPrimitiveGroundPlane extends SDFPrimitive {

	private float previewSize = 200;


	public SDFPrimitiveGroundPlane(float height, Material material) {
		Matrix4d base = new Matrix4d();
		base.m32(height);
		frame = new Matrix4dAnimated(base, "Ground");

		this.material = material;

		displayName = "PrimGround";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		Vector3d vl = getVectorLocal(v, frame.getInvert(time), context);
		return vl.z;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		gd.add(new Plane(frame.get(time), getPrimitiveColor(solid, materialPreview), 100));
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		sourceRepresentationBackground(definitions, time);
		
		return "SDFPrimitive.getVectorLocal(" + vLocalLast + ", " + compileNameMatrixInvert + ", context).z";
	}

}
