package render_sdf.sdf;

import static java.lang.Math.abs;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveStar extends SDF {
	private Matrix4d frame;
	private Matrix4d frameInvert;
	private float size;


	public SDFPrimitiveStar(Vector3d position, float size, Material material) {
		this.frame =  new Matrix4d().setColumn(3, new Vector4d(position,1));
		this.frameInvert = new Matrix4d(frame).invert();
		this.size = size;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(Vector3d v) {
		Vector3d vLocal = v.mulPosition(frameInvert, new Vector3d());
		double ax = abs(vLocal.x);
		double ay = abs(vLocal.y);
		double az = abs(vLocal.z);

		return (new DistanceData((ax * ay * az) + (ax + ay + az) - size, this.material));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		Group g = new Group();

		float hp = (float) (size / 2);

		Color c = getPrimitiveColor(solid, materialPreview);

		g.add(new Line(new Vector3d(-hp, 0, 0), new Vector3d(hp, 0, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, -hp, 0), new Vector3d(0, hp, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, 0, -hp), new Vector3d(0, 0, hp)).setFillColor(c));

		g.setFrame(frame);

		gd.add(g);
	}

}
