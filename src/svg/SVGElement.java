package svg;

import java.util.ArrayList;

import data.GeometryFile;

public abstract class SVGElement {
	
	public int strokeColor;
	public int fillColor;
	
	/**
	 * Pushes this element into the geometry list of the scene.
	 */
	public abstract void bake(GeometryFile geom);
	
	/**
	 * Returns the relevant KRL code for plotting. 
	 */
	public abstract void plot(ArrayList<String> out);
}