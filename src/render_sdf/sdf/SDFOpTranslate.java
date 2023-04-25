package render_sdf.sdf;


import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;

public class SDFOpTranslate extends SDF {
	private Matrix4d frame;
	private Matrix4d frameInvert;


	public SDFOpTranslate(SDF child, Vector3d position) {
		this.frame = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		this.frameInvert = new Matrix4d(frame).invert();
		this.childA = child;

		displayName = "OpTranslate";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		Vector3d vLocal = frameInvert.transformPosition(v, new Vector3d());
		return childA.getDistance(vLocal, time);
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		childA.extractSceneGeometry(gd, solid, materialPreview, time);
	}


	@Override
	public Animated[] getAnimated() {
		// TODO Auto-generated method stub
		return null;
	}

}
