package svg;

import java.util.ArrayList;

import geometry.Line;

import org.w3c.dom.Element;

import utility.PVector;

import data.GeometryFile;


public class SVGRect extends SVGElement {

	float x;
	float y;
	float width;
	float height;
	
	public SVGRect(Element e) {
		x = Float.valueOf(e.getAttribute("x"));
		y = Float.valueOf(e.getAttribute("y"));
		width = Float.valueOf(e.getAttribute("width"));
		height = Float.valueOf(e.getAttribute("height"));
		
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
		Line l1 = new Line(new PVector(x,y),new PVector(x+width,y));
		l1.color(strokeColor);
		Line l2 = new Line(new PVector(x+width,y),new PVector(x+width,y+height));
		l2.color(strokeColor);
		Line l3 = new Line(new PVector(x+width,y+height),new PVector(x,y+height));
		l3.color(strokeColor);
		Line l4 = new Line(new PVector(x,y+height),new PVector(x,y));
		l4.color(strokeColor);
		geom.add(l1);
		geom.add(l2);
		geom.add(l3);
		geom.add(l4);	
	}

	@Override
	public void plot(ArrayList<String> out) {
		System.out.println("Implement exporting in SVGRect!");
		
	}
	
}
