package svg;

import org.w3c.dom.Element;

import geometry.GeometryDatabase;
import geometry.Line;

import utility.PVector;
import utility.Util;

public class SVGLine extends SVGElement {
	private PVector start;
	private PVector end;
	
	public SVGLine(Element e) {
		super(e);
		start = new PVector( Float.valueOf(e.getAttribute("x1")), Float.valueOf(e.getAttribute("y1")));
		end = new PVector( Float.valueOf(e.getAttribute("x2")), Float.valueOf(e.getAttribute("y2")));
	}

	@Override
	public void bake(GeometryDatabase geom) {
		Line l = new Line(start,end);
		l.setColor(Util.red(strokeColor),Util.green(strokeColor),Util.blue(strokeColor));
		geom.add(l);
	}
	
	@Override
	public String toString() {
		return(start.toString() + " " + end.toString());
	}	
}
