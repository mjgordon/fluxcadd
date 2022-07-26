package render_sdf.sdf;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;
import utility.PVector;
import utility.PVectorD;

public class SDFPrimitiveGroundPlane extends SDF {
	private double height;
	
	private float previewSize = 200;
	
	
	public SDFPrimitiveGroundPlane(float height, Material material) {
		this.height = height;
		this.material = material;
	}

	
	@Override
	public DistanceData getDistance(PVectorD v) {
		return(new DistanceData(v.z - height, this.material));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid) {
		Group g = new Group();
		
		float hp = previewSize / 2;
		
		Color c = solid ? previewColorSolid : previewColorVoid;
		
		int gridSize = 10;
		for (int i = 0; i <= gridSize; i++) {
			float n = previewSize / gridSize * i - hp;
			g.add(new Line(new PVector(-hp,n,height), new PVector(hp,n,height)).setColor(c));	
			g.add(new Line(new PVector(n,-hp,height), new PVector(n,hp,height)).setColor(c));	
		}
		
		gd.add(g);
		
	}
	
	
}
