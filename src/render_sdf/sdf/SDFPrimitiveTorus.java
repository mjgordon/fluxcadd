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
import utility.Color3i;

public class SDFPrimitiveTorus extends SDF {
	private Matrix4dAnimated frame;
	
	private double ringRadius;
	private double profileRadius;
	
	
	public SDFPrimitiveTorus(Vector3d position, double ringRadius, double profileRadius, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		frame = new Matrix4dAnimated(base, "Torus");
		this.ringRadius = ringRadius;
		this.profileRadius = profileRadius;
		this.material = material;
		
		displayName = "PrimTorus";
	}
	
	
	public SDFPrimitiveTorus(Matrix4d base, double ringRadius, double profileRadius, Material material) {
		frame = new Matrix4dAnimated(base, "Torus");
		this.ringRadius = ringRadius;
		this.profileRadius = profileRadius;
		this.material = material;
		
		displayName = "PrimTorus";
	}

	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vFrame = frame.getInvert(time).transformPosition(new Vector3d(v));
		Vector3d ringPos = new Vector3d(vFrame).setComponent(2,0).normalize().mul(ringRadius);
		
		return vFrame.distance(ringPos) - profileRadius;
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		Color3i color = getPrimitiveColor(solid, materialPreview);
		
		int minorCount = 16;
		int majorCount = 8;
		
		for (int i = 0; i < minorCount; i++) {
			double n = Math.PI * 2 * i / minorCount;
			double n2 = Math.PI * 2 * (i + 1) / minorCount;
			
			for (int j = 0; j < majorCount; j++) {
				double angA = Math.PI * 2 * j / majorCount;
				double angB = Math.PI * 2 * (j + 1) / majorCount;
				Vector3d a = new Vector3d(Math.cos(angA) * profileRadius, Math.sin(angA) * profileRadius,0);
				Vector3d b = new Vector3d(Math.cos(angB) * profileRadius, Math.sin(angB) * profileRadius,0);
				Vector3d c = new Vector3d(Math.cos(angA) * profileRadius, Math.sin(angA) * profileRadius,0);
				
				a.rotateX(Math.PI / 2);
				b.rotateX(Math.PI / 2);
				c.rotateX(Math.PI / 2);
				
				a.x += ringRadius;
				b.x += ringRadius;
				c.x += ringRadius;
				
				a.rotateZ(n);
				b.rotateZ(n);
				c.rotateZ(n2);
				
				g.add(new Line(a,b).setFillColor(color));
				g.add(new Line(a,c).setFillColor(color));
			}
		}
		
		g.setMatrix(frame);

		gd.add(g);
	}
	
	@Override
	public Animated[] getAnimated() {
		return new Animated[] {frame};
	}
	

}