package geometry;

import java.util.ArrayList;
import java.util.UUID;

import utility.PVector;
import utility.Color;


/** 
 * Geometry existing in 2d or 3d space. Can be made of arbitrary structures of other Geometry
 *
 */
public abstract class Geometry {
	
	public UUID guid;
	
	public String name;
	
	public boolean visible = true;
	
	protected Color color;
	
	protected PVector position = new PVector();
	protected PVector size = new PVector();
	
	public Geometry setColor(int r, int g, int b) {
		this.color.r = r;
		this.color.g = g;
		this.color.b = b;
		
		return this;
	}
	
	public Geometry setColor(Color c) {
		setColor(c.r,c.g,c.b);
		
		return this;
	}
	
	public abstract void render();
	
	//TODO: FEATURE : Each geometry type should have an individual resolution setting
	public abstract ArrayList<PVector> getVectorRepresentation(float resolution);
	
	public abstract ArrayList<Line> getHatchLines();
	
}
