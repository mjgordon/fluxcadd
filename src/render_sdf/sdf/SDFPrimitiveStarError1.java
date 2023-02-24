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

/**
 * this is no longer a mystery now that the issue with SDFOpFillet was figured
 * out (x*y creates a non-linear distance function). Not clear if it will be
 * useful in the future.
 * 
 * @author mattj
 *
 */
public class SDFPrimitiveStarError1 extends SDF {
	private Matrix4d frame;
	private Matrix4d frameInvert;
	private double size;


	public SDFPrimitiveStarError1(Vector3d position, double size, Material material) {
		this.frame = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		this.frameInvert = new Matrix4d(frame).invert();
		this.size = size;

		this.material = material;
		
		displayName = "PrimStarError0";
	}


	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		Vector3d vLocal = v.mulPosition(frameInvert, new Vector3d());

		double ax = abs(vLocal.x);
		double ay = abs(vLocal.y);
		double az = abs(vLocal.z);

		//return (new DistanceData(Math.max((ax * ay * az) - size, 0.0001001), this.material));
		return (new DistanceData((ax * ay * az) - size, this.material));
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
