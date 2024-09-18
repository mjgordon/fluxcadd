package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveGroundPlane extends SDF {
	private Matrix4dAnimated frame;

	private float previewSize = 200;


	public SDFPrimitiveGroundPlane(float height, Material material) {
		Matrix4d base = new Matrix4d();
		base.m32(height);
		frame = new Matrix4dAnimated(base, "Ground");
		
		this.material = material;
		
		displayName = "PrimGround";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vLocal = v.mulPosition(frame.getInvert(time), new Vector3d());
		return vLocal.z;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		float hp = previewSize / 2;

		Color c = getPrimitiveColor(solid, materialPreview);

		int gridSize = 10;
		for (int i = 0; i <= gridSize; i++) {
			float n = previewSize / gridSize * i - hp;
			g.add(new Line(new Vector3d(-hp, n, 0), new Vector3d(hp, n, 0)).setFillColor(c));
			g.add(new Line(new Vector3d(n, -hp, 0), new Vector3d(n, hp, 0)).setFillColor(c));
		}

		g.setFrame(frame);

		gd.add(g);
	}
	
	@Override
	public Animated[] getAnimated() {
		return new Animated[] {frame};
	}
	

}
