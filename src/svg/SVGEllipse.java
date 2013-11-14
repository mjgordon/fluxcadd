package svg;

import java.util.ArrayList;

import org.w3c.dom.Element;

import geometry.Shape;
import utility.PVector;
import utility.Util;
import data.GeometryFile;

public class SVGEllipse extends SVGElement {
	
	public float x;
	public float y;
	public float width;
	public float height;
	
	public int resolution = 30;

	public SVGEllipse(Element e) {
		x = Float.valueOf(e.getAttribute("cx"));
		y = Float.valueOf(e.getAttribute("cy"));
		if (e.getAttribute("r").equals("")) {
			width = Float.valueOf(e.getAttribute("rx"));
			height = Float.valueOf(e.getAttribute("ry"));
		}
		else {
			width = Float.valueOf(e.getAttribute("r"));
			height = width;
		}
		
		String strokeString = e.getAttribute("stroke");
		if ( (strokeString != null) && (strokeString.equals("none") == false)) {
			strokeColor = Integer.parseInt(e.getAttribute("stroke").substring(1),16);
		}
		
		String fillString = e.getAttribute("fill");
		if ( (fillString != null) && (fillString.equals("none") == false)) {
			fillColor = Integer.parseInt(e.getAttribute("fill").substring(1),16);
		}
	}
	
	@Override
	public void bake(GeometryFile geom) {
		ArrayList<PVector> points = new ArrayList<PVector>();
		for (int i = 0; i< resolution; i++) {
			float f = ((float)i / resolution) * Util.TWO_PI;
			PVector v = new PVector((float)(x + (Math.cos(f) * width)),(float)(y + (Math.sin(f) * height)));
			points.add(v);
		}
		Shape shape = new Shape(points,1,1,1);
		geom.add(shape);
		
	}

	@Override
	public void plot(ArrayList<String> out) {
		System.out.println("Implement plotting in SVGEllipse!");
		
	}
}
