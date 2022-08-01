package svg;

import org.joml.Vector3d;
import org.w3c.dom.Element;

import geometry.GeometryDatabase;
import geometry.Line;

public class SVGLine extends SVGElement {
	private Vector3d start;
	private Vector3d end;


	public SVGLine(Element e) {
		super(e);
		start = new Vector3d(Double.valueOf(e.getAttribute("x1")), Double.valueOf(e.getAttribute("y1")), 0);
		end = new Vector3d(Double.valueOf(e.getAttribute("x2")), Double.valueOf(e.getAttribute("y2")), 0);
	}


	@Override
	public void bake(GeometryDatabase geom) {
		Line l = new Line(start, end);
		l.setColor(strokeColor);
		geom.add(l);
	}


	@Override
	public String toString() {
		return (start.toString() + " " + end.toString());
	}
}
