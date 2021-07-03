package svg;

import org.w3c.dom.Element;

import geometry.GeometryDatabase;
import geometry.Rect;

public class SVGRect extends SVGElement {

	private float x;
	private float y;
	private float width;
	private float height;

	public SVGRect(Element e) {
		super(e);

		x = Float.valueOf(e.getAttribute("x"));
		y = Float.valueOf(e.getAttribute("y"));
		width = Float.valueOf(e.getAttribute("width"));
		height = Float.valueOf(e.getAttribute("height"));
	}

	@Override
	public void bake(GeometryDatabase geom) {
		Rect rect = new Rect(x, y,0, width, height);
		rect.setColor(this.strokeColor);

		geom.add(rect);
	}
}
