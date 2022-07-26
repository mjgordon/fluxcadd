package render_sdf.sdf;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;
import utility.PVector;
import utility.PVectorD;

public class SDFPrimitiveCube extends SDF {
	private PVectorD position;
	private double size;


	public SDFPrimitiveCube(PVectorD position, double size, Material material) {
		this.position = position;
		this.size = size;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(PVectorD v) {
		PVectorD dist = PVectorD.sub(v, position);

		return (new DistanceData(Math.max(Math.abs(dist.x), Math.max(Math.abs(dist.y), Math.abs(dist.z))) - size, this.material));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		Group g = new Group();
		
		float hp = (float) (size / 2);
		
		Color c = solid ? previewColorSolid : previewColorVoid;
		
		g.add(new Line(new PVector(-hp,-hp,-hp), new PVector(hp,-hp,-hp)).setColor(c));
		g.add(new Line(new PVector(-hp,hp,-hp), new PVector(hp,hp,-hp)).setColor(c));
		g.add(new Line(new PVector(-hp,-hp,hp), new PVector(hp,-hp,hp)).setColor(c));
		g.add(new Line(new PVector(-hp,hp,hp), new PVector(hp,hp,hp)).setColor(c));
		
		g.add(new Line(new PVector(-hp,-hp,-hp), new PVector(-hp,hp,-hp)).setColor(c));
		g.add(new Line(new PVector(hp,-hp,-hp), new PVector(hp,hp,-hp)).setColor(c));
		g.add(new Line(new PVector(-hp,-hp,hp), new PVector(-hp,hp,hp)).setColor(c));
		g.add(new Line(new PVector(hp,-hp,hp), new PVector(hp,hp,hp)).setColor(c));
		
		g.add(new Line(new PVector(-hp,-hp,-hp), new PVector(-hp,-hp,hp)).setColor(c));
		g.add(new Line(new PVector(hp,-hp,-hp), new PVector(hp,-hp,hp)).setColor(c));
		g.add(new Line(new PVector(-hp,hp,-hp), new PVector(-hp,hp,hp)).setColor(c));
		g.add(new Line(new PVector(hp,hp,-hp), new PVector(hp,hp,hp)).setColor(c));
		
		g.frame.m03 = (float) position.x;
		g.frame.m13 = (float) position.y;
		g.frame.m23 = (float) position.z;
		
		gd.add(g);
	}
}
