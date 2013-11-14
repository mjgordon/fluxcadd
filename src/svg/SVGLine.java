package svg;

import java.util.ArrayList;

import geometry.Line;

import org.w3c.dom.Element;

import data.GeometryFile;

import utility.PVector;
import utility.Util;

public class SVGLine extends SVGElement {
	PVector start;
	PVector end;
	
	public SVGLine(Element e) {
		start = new PVector( Float.valueOf(e.getAttribute("x1")), Float.valueOf(e.getAttribute("y1")));
		end = new PVector( Float.valueOf(e.getAttribute("x2")), Float.valueOf(e.getAttribute("y2")));
		
		String strokeString = e.getAttribute("stroke");
		if ( (strokeString != null) && (strokeString.equals("none") == false)) {
			strokeColor = Integer.parseInt(e.getAttribute("stroke").substring(1));
		}
		
	}

	@Override
	public void bake(GeometryFile geom) {
		Line l = new Line(start,end);
		l.color(Util.red(strokeColor),Util.green(strokeColor),Util.blue(strokeColor));
		geom.add(l);
		
	}

	@Override
	public void plot(ArrayList<String> out) {
		System.out.println("Implement plotting in SVGLine!");
		
	}
	
	public String toString() {
		return(start.toString() + " " + end.toString());
	}
}
