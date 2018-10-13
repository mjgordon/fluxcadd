package geometry;

import java.util.ArrayList;

import utility.PVector;
import utility.Util;

public abstract class Geometry {
	
	public String name;
	
	public boolean visible = true;
	
	/**
	 * TODO: FEATURE : More robust system for internal geometry ids
	 */
	public static long count = 0;
	
	protected float r = 1;
	protected float g = 1;
	protected float b = 1;
	
	protected PVector position = new PVector();
	protected PVector size = new PVector();
	
	public Geometry() {
		name = "geometry_" + count;
		count++;
	}
	
	public void setColor(int c) {
		r = Util.red(c);
		g = Util.green(c);
		b = Util.blue(c);
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
