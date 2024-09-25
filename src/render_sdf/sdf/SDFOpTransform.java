package render_sdf.sdf;


import org.joml.Matrix4d;
import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;

public class SDFOpTransform extends SDF {
	private Matrix4dAnimated frame;


	public SDFOpTransform(SDF child) {
		Matrix4d base = new Matrix4d();
		this.frame = new Matrix4dAnimated(base, "Transform");
		
		this.childA = child;

		displayName = "OpTranslate";
	}
	
	
	public SDFOpTransform(SDF child, Vector3d vector) {
		Matrix4d base = new Matrix4d().setTranslation(vector);
		this.frame = new Matrix4dAnimated(base, "Transform");
		
		this.childA = child;

		displayName = "OpTranslate";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vLocal = frame.getInvert(time).transformPosition(v, new Vector3d());
		return childA.getDistance(vLocal, time);
	}
	
	
	@Override
	public Material getMaterial(Vector3d v, double time) {
		Vector3d vLocal = frame.getInvert(time).transformPosition(v, new Vector3d());
		return childA.getMaterial(vLocal, time);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] {frame};
	}
	
	public SDFOpTransform addKeyframe(double timestamp, Matrix4d m) {
		frame.addKeyframe(timestamp, m);
		return this;
	}

}
