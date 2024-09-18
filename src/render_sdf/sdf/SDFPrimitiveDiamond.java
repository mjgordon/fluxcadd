package render_sdf.sdf;

import static java.lang.Math.abs;


import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveDiamond extends SDF {
	private Matrix4dAnimated frame;

	private double size;


	public SDFPrimitiveDiamond(Vector3d position, double size, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position,1));
		frame = new Matrix4dAnimated(base, "Diamond");
		
		this.size = size;
		this.material = material;
		
		displayName = "PrimDiamond";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vLocal = v.mulPosition(frame.getInvert(time),new Vector3d());

		return abs(vLocal.x) + abs(vLocal.y) + abs(vLocal.z) - size;
	}
	
	
	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();
		
		float hp = (float) (size / 2);
		
		Color c = getPrimitiveColor(solid, materialPreview);
		
		g.add(new Line(new Vector3d(-hp,0,0), new Vector3d(hp,0,0)).setFillColor(c));
		g.add(new Line(new Vector3d(0,-hp,0), new Vector3d(0,hp,0)).setFillColor(c));
		g.add(new Line(new Vector3d(0,0,-hp), new Vector3d(0,0,hp)).setFillColor(c));
		
		g.setFrame(frame);
		
		gd.add(g);
	}
	
	@Override
	public Animated[] getAnimated() {
		return new Animated[] {frame};
	}

}
