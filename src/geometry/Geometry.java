package geometry;

import java.util.ArrayList;
import java.util.UUID;

import utility.PVector;
import utility.Util;

/** 
 * Geometry existing in 2d or 3d space. Can be made of arbitrary structures of other Geometry
 *
 */
public abstract class Geometry {
	
	public UUID guid;
	
	public String name;
	
	public boolean visible = true;
	
	protected float r = 1;
	protected float g = 1;
	protected float b = 1;
	
	protected PVector position = new PVector();
	protected PVector size = new PVector();
	
	public Geometry setColor(int c) {
		r = Util.red(c);
		g = Util.green(c);
		b = Util.blue(c);
		return(this);
	}
	
	public void setColor(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public int getColor() {
		return(Util.getColor(r, g, b));
	}
	
	public abstract void render();
	
	//TODO: FEATURE : Each geometry type should have an individual resolution setting
	public abstract ArrayList<PVector> getVectorRepresentation(float resolution);
	
	public abstract ArrayList<Line> getHatchLines();
	
}
