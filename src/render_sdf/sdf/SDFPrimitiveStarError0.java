package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;
import utility.Color3i;


public class SDFPrimitiveStarError0 extends SDFPrimitive {
	private double size;


	public SDFPrimitiveStarError0(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "StarE0");

		this.size = size;
		this.material = material;

		displayName = "PrimStarError0";
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		return distanceFunction(v, frame.getInvert(time), size, context);
	}
	
	
	public static double distanceFunction(Vector3d v, Matrix4d frameInvert, double size, VectorContext context) {
		Vector3d vl = getVectorLocal(v, frameInvert, context).absolute();
		return (vl.x * vl.y * vl.z) + (vl.x + vl.y + vl.z) - size;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		float hp = (float) (size / 2);

		Color3i c = getPrimitiveColor(solid, materialPreview);

		g.add(new Line(new Vector3d(-hp, 0, 0), new Vector3d(hp, 0, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, -hp, 0), new Vector3d(0, hp, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, 0, -hp), new Vector3d(0, 0, hp)).setFillColor(c));

		g.setMatrix(frame);

		gd.add(g);
	}


	@Override
	public Animated[] getAnimated() {
		return new Animated[] { frame };
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions, ArrayList<String> transforms, String vLocalLast, double time) {
		sourceRepresentationBackground(definitions, time);
		
		return "SDFPrimitiveStarError0.distanceFunction(" + vLocalLast + ", " + compileNameMatrixInvert + ", " + size + ", context)";
	}
}
