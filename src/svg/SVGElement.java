package svg;

import geometry.GeometryDatabase;

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
		
		String styleString = e.getAttribute("style");
		if ( (styleString.length() > 0) && (styleString.equals("none") == false)) {
			String[] elements = styleString.split(";");
			for (String s : elements) {
				String[] parts = s.split(":");
				if (parts[0].equals("fill") && !parts[1].equals("none")) {
					filled = true;
					fillColor = Integer.parseInt(parts[1].substring(1),16);
				}
				else if (parts[0].equals("stroke") && !parts[1].equals("none")) {
					stroked = true;
					strokeColor = Integer.parseInt(parts[1].substring(1),16);
				}
			}
		}
	}
	
	/**
	 * Pushes this element into the geometry list of the scene.
	 */
	public abstract void bake(GeometryDatabase geom);
}