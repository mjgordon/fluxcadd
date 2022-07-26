package render_sdf.sdf;


import static java.lang.Math.abs;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;
import utility.PVector;
import utility.PVectorD;


/**
 * this is no longer a mystery now that the issue with SDFOpFillet was figured out (x*y creates a non-linear distance function). Not clear if it will be useful in the future. 
 * @author mattj
 *
 */
public class SDFPrimitiveFuckedStar extends SDF {
	private PVectorD position;
	private double size;
	
	public SDFPrimitiveFuckedStar(PVectorD position, double size, Material material) {
		this.position = position;
		this.size = size;
		
		this.material = material;
	}

	
	@Override
	public DistanceData getDistance(PVectorD v) {
		double ax = abs(v.x - position.x);
		double ay = abs(v.y - position.y);
		double az = abs(v.z - position.z);
		
		return(new DistanceData( Math.max((ax * ay) - size,0.0001001), this.material));  
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
