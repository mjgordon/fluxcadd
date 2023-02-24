package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveGroundPlane extends SDF {
	private Matrix4d frame;
	private Matrix4d frameInvert;

	private float previewSize = 200;


	public SDFPrimitiveGroundPlane(float height, Material material) {
		this.frame = new Matrix4d();
		frame.m32(height);
		frameInvert = frame.invert(new Matrix4d());

		this.material = material;
		
		displayName = "PrimGround";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		Vector3d vLocal = v.mulPosition(frameInvert, new Vector3d());
		return (new DistanceData(vLocal.z, this.material));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
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
	

}
