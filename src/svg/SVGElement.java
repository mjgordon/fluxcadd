package svg;

import geometry.GeometryDatabase;
import utility.Color;

import org.w3c.dom.Element;


public abstract class SVGElement {
	
	public Color strokeColor = null;
	public Color fillColor = null;
	
	public SVGElement(Element e) {

		String strokeString = e.getAttribute("stroke");
		if ( (strokeString.length() > 0) && (strokeString.equals("none") == false)) {
			strokeColor = new Color(Integer.parseInt(strokeString.substring(1),16));
		}
		
		String fillString = e.getAttribute("fill");
		if ( (fillString.length() > 0) && (fillString.equals("none") == false)) {
			fillColor = new Color(Integer.parseInt(fillString.substring(1),16));
		}
		
		String styleString = e.getAttribute("style");
		if ( (styleString.length() > 0) && (styleString.equals("none") == false)) {
			String[] elements = styleString.split(";");
			for (String s : elements) {
				String[] parts = s.split(":");
				if (parts[0].equals("fill") && !parts[1].equals("none")) {
					fillColor = new Color(Integer.parseInt(parts[1].substring(1),16));
				}
				else if (parts[0].equals("stroke") && !parts[1].equals("none")) {
					strokeColor = new Color(Integer.parseInt(parts[1].substring(1),16));
				}
			}
		}
	}
	
	/**
	 * Pushes this element into the geometry list of the scene.
	 */
	public abstract void bake(GeometryDatabase geom);
}