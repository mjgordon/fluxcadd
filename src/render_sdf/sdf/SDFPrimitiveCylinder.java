package render_sdf.sdf;

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

public class SDFPrimitiveCylinder extends SDF {
	private Matrix4dAnimated frame;
	double radius;
	double height;
	double halfHeight;
	
	public SDFPrimitiveCylinder(Vector3d position, double radius, double height, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		base.scale(radius,radius,height / 2);
		frame = new Matrix4dAnimated(base, "Cylinder");
		
		this.material = material;
		
		this.radius = radius;
		this.height = height;
		this.halfHeight = height * 0.5;
		
		displayName = "PrimCylinder";
	}
	
	public SDFPrimitiveCylinder(Matrix4d base, double radius, double height, Material material) {
		base.scale(radius,radius,height / 2);
		frame = new Matrix4dAnimated(base, "Cylinder");
		
		this.material = material;
		
		this.radius = radius;
		this.height = height;
		this.halfHeight = height * 0.5;
		
		displayName = "PrimCylinder";
	}
	

	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vl = v.mulPosition(frame.getInvertNormal(time), new Vector3d());
		
		// Closest to curved face
		if (vl.z > - halfHeight && vl.z < halfHeight) {
			return Math.sqrt((vl.x * vl.x) + (vl.y * vl.y)) - radius;
		}
		// Closest to end faces
		else if (Math.sqrt((vl.x * vl.x) + (vl.y * vl.y)) < radius && (vl.z < -halfHeight || vl.z > halfHeight)) {
			return Math.abs(vl.z) - halfHeight;
		}
		// Closest to edge
		else {
			Vector3d edgePos = new Vector3d(vl).setComponent(2,0).normalize(radius).setComponent(2, halfHeight * Math.signum(vl.z));
			return vl.distance(edgePos);
		}
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		Color color = getPrimitiveColor(solid, materialPreview);
		
		int count = 16;
		
		for (int i = 0; i < count; i++) {
			double n = Math.PI * 2 * i / count;
			double n2 = Math.PI * 2 * (i + 1) / count;
			
			double x = Math.cos(n);
			double y = Math.sin(n);
			double x2 = Math.cos(n2);
			double y2 = Math.sin(n2);
			
			g.add(new Line(new Vector3d(x,y,1), new Vector3d(x,y,-1)).setFillColor(color));
			
			g.add(new Line(new Vector3d(x,y,1), new Vector3d(0,0,1)).setFillColor(color));
			g.add(new Line(new Vector3d(x,y,-1), new Vector3d(0,0,-1)).setFillColor(color));
			
			g.add(new Line(new Vector3d(x,y,1), new Vector3d(x2,y2,1)).setFillColor(color));
			g.add(new Line(new Vector3d(x,y,-1), new Vector3d(x2,y2,-1)).setFillColor(color));
		}
		
		
		g.setMatrix(frame);

		gd.add(g);
		
	}

	@Override
	public Animated[] getAnimated() {
		return new Animated[] {frame};
	}

}
