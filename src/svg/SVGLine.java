package svg;

import java.util.ArrayList;

import geometry.Line;

import org.w3c.dom.Element;

import robocam.Module_Plotter;

import data.GeometryFile;

import utility.PVector;
import utility.Util;

public class SVGLine extends SVGElement {
	PVector start;
	PVector end;
	
	public SVGLine(Element e) {
		super(e);
		
		start = new PVector( Float.valueOf(e.getAttribute("x1")), Float.valueOf(e.getAttribute("y1")));
		end = new PVector( Float.valueOf(e.getAttribute("x2")), Float.valueOf(e.getAttribute("y2")));
		
	}

	@Override
	public void bake(GeometryFile geom, float hatchWidth) {
		Line l = new Line(start,end);
		l.color(Util.red(strokeColor),Util.green(strokeColor),Util.blue(strokeColor));
		geom.add(l);
		
	}

	@Override
	public void plot(ArrayList<String> out, float hatchWidth) {
		float s;
		if (Module_Plotter.canvasHeight < Module_Plotter.canvasWidth)
			s = 279.4f / Module_Plotter.canvasWidth;
		else
			s = 279.4f / Module_Plotter.canvasHeight;
		
		out.add("LIN {X " + start.x * s + " ,Y " + start.y * s + " ,Z -10}");
		out.add("LIN {Z 0}");
		out.add("LIN {X " + end.x * s + " ,Y " + end.y * s + "}");
		out.add("LIN {Z -10}");
	}
	
	public String toString() {
		return(start.toString() + " " + end.toString());
	}
}
