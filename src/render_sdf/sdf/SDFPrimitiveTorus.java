package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveTorus extends SDF {
	
	private Matrix4d frame;
	private Matrix4d frameInvert;
	private double ringRadius;
	private double profileRadius;
	
	
	public SDFPrimitiveTorus(Vector3d position, double ringRadius, double profileRadius, Material material) {
		this.frame = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		this.frameInvert = new Matrix4d(frame).invert();
		this.ringRadius = ringRadius;
		this.profileRadius = profileRadius;
		this.material = material;
	}
	
	
	public SDFPrimitiveTorus(Matrix4d frame, double ringRadius, double profileRadius, Material material) {
		this.frame = frame;
		this.frameInvert = new Matrix4d(frame).invert();
		this.ringRadius = ringRadius;
		this.profileRadius = profileRadius;
		this.material = material;
	}

	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		//TODO: Check which version is faster
		Vector3d vFrame = frameInvert.transformPosition(new Vector3d(v));
		
		Vector3d ringPos = new Vector3d(vFrame).setComponent(2,0).normalize().mul(ringRadius);
		
		/*
		double angle = Math.atan2(vFrame.y, vFrame.x) + (Math.PI / 2);
		Vector3d xBasis = new Vector3d(vFrame).sub(ringPos).normalize();
		Vector3d yBasis = new Vector3d(Math.cos(angle), Math.sin(angle),0);
		Vector3d zBasis = xBasis.cross(yBasis, new Vector3d());
		
		Matrix4d profileInvert = new Matrix4d().identity();
		profileInvert.setColumn(0,new Vector4d(xBasis, 0));
		profileInvert.setColumn(1,new Vector4d(yBasis, 0));
		profileInvert.setColumn(2,new Vector4d(zBasis, 0));
		profileInvert.setColumn(3,new Vector4d(ringPos,1));
		profileInvert.invert();
		
		Vector3d vProfile = profileInvert.transformPosition(new Vector3d(vFrame));
		
		return (new DistanceData(vProfile.x - profileRadius, this.material));
		*/
		return (new DistanceData(vFrame.distance(ringPos) - profileRadius, this.material));
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		Group g = new Group();

		Color color = getPrimitiveColor(solid, materialPreview);
		
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
		
		g.setFrame(frame);

		gd.add(g);
	}
	
	@Override
	public String describeTree(String input, int depth) {
		input += "\n";
		input += " ".repeat(depth);
		input += "PrimTorus";
		return input;
	}
}