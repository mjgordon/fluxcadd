package render_sdf.sdf;

import static java.lang.Math.abs;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;
import utility.PVector;
import utility.PVectorD;

public class SDFPrimitiveDiamond extends SDF {
	private PVectorD position;
	private double size;


	public SDFPrimitiveDiamond(PVectorD position, float size, Material material) {
		this.position = position;
		this.size = size;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(PVectorD v) {
		PVectorD dist = PVectorD.sub(position, v);

		return (new DistanceData(abs(dist.x) + abs(dist.y) + abs(dist.z) - size, this.material));

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
