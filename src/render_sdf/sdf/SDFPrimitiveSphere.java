package render_sdf.sdf;

import java.util.ArrayList;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import geometry.Polyline;
import render_sdf.material.Material;
import utility.Color;
import utility.PVector;
import utility.PVectorD;
import utility.Util;

public class SDFPrimitiveSphere extends SDF {
	private PVectorD position;
	private double radius;
	
	public SDFPrimitiveSphere(PVectorD position,double radius, Material material) {
		this.position = position;
		this.radius = radius;
		this.material = material;
	}

	@Override
	public DistanceData getDistance(PVectorD v) {
		return( new DistanceData(PVectorD.dist(v, position) - radius, this.material));
	}
	
	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		Group g = new Group();
		
		int segments = 36;
	
		PVector[] verticesX = new PVector[segments + 1];
		PVector[] verticesY = new PVector[segments + 1];
		PVector[] verticesZ = new PVector[segments + 1];
		
		for (int i = 0; i <= segments; i++) {
			float n = 1.0f * i / segments * Util.TWO_PI;
			
			float cosVal = (float) (Math.cos(n) * radius);

			float sinVal = (float) (Math.sin(n) * radius);
			
			verticesX[i] = new PVector(0,cosVal,sinVal);
			verticesY[i] = new PVector(cosVal,0,sinVal);
			verticesZ[i] = new PVector(cosVal,sinVal,0);
		}
		
		Color c = solid ? previewColorSolid : previewColorVoid;
		
		g.add(new Polyline(verticesX).setColor(c));
		g.add(new Polyline(verticesY).setColor(c));
		g.add(new Polyline(verticesZ).setColor(c));
		
		g.frame.m03 = (float) position.x;
		g.frame.m13 = (float) position.y;
		g.frame.m23 = (float) position.z;
		
		gd.add(g);
	}
}
