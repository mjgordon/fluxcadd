package svg;

import java.util.ArrayList;

import lisp.GeometryFile;

import org.w3c.dom.Element;


public abstract class SVGElement {
	
	public boolean stroked = false;
	public boolean filled = false;
	
	public int strokeColor;
	public int fillColor;
	
	public SVGElement(Element e) {

		String strokeString = e.getAttribute("stroke");
		if ( (strokeString.length() > 0) && (strokeString.equals("none") == false)) {
			stroked = true;
			strokeColor = Integer.parseInt(strokeString.substring(1),16);
		}
		
		String fillString = e.getAttribute("fill");
		if ( (fillString.length() > 0) && (fillString.equals("none") == false)) {
			filled = true;
			fillColor = Integer.parseInt(fillString.substring(1),16);
		}
	}
	
	/**
	 * Pushes this element into the geometry list of the scene.
	 */
	public abstract void bake(GeometryFile geom,float hatchWidth);
	
	/**
	 * Returns the relevant KRL code for plotting. 
	 */
	public abstract void plot(ArrayList<String> out,float hatchWidth);
}