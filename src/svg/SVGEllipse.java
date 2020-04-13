package svg;

import org.w3c.dom.Element;

import geometry.Ellipse;
import geometry.GeometryDatabase;

public class SVGEllipse extends SVGElement {

	private float x;
	private float y;
	private float width;
	private float height;

	public SVGEllipse(Element e) {
		super(e);

		x = Float.valueOf(e.getAttribute("cx"));
		y = Float.valueOf(e.getAttribute("cy"));
		if (e.getAttribute("r").equals("")) {
			width = Float.valueOf(e.getAttribute("rx"));
			height = Float.valueOf(e.getAttribute("ry"));
		} else {
			width = Float.valueOf(e.getAttribute("r"));
			height = width;
		}
	}

	@Override
	public void bake(GeometryDatabase geom) {
		Ellipse ellipse = new Ellipse(x, y, width, height);
		ellipse.setColor(strokeColor);
		geom.add(ellipse);
	}
}
