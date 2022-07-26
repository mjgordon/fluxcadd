package render_sdf.sdf;

import static java.lang.Math.abs;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;
import utility.PVector;
import utility.PVectorD;

public class SDFPrimitiveStar extends SDF {
	private PVectorD position;
	private float size;
	
	
	public SDFPrimitiveStar(PVectorD position, float size, Material material) {
		this.position = position;
		this.size = size;
		this.material = material;
	}

	
	@Override
	public DistanceData getDistance(PVectorD v) {
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);
		
		return(new DistanceData((ax * ay * az) + (ax + ay + az ) - size, this.material));  	
	}
	
	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		Group g = new Group();
		
		float hp = (float) (size / 2);
		
		Color c = solid ? previewColorSolid : previewColorVoid;
		
		g.add(new Line(new PVector(-hp,0,0), new PVector(hp,0,0)).setColor(c));
		g.add(new Line(new PVector(0,-hp,0), new PVector(0,hp,0)).setColor(c));
		g.add(new Line(new PVector(0,0,-hp), new PVector(0,0,hp)).setColor(c));
		
		g.frame.m03 = (float) position.x;
		g.frame.m13 = (float) position.y;
		g.frame.m23 = (float) position.z;
		
		gd.add(g);
	}

}
