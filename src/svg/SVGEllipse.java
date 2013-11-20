package svg;

import java.util.ArrayList;

import org.w3c.dom.Element;

import robocam.Module_Plotter;

import geometry.Line;
import utility.PVector;
import utility.Util;
import data.GeometryFile;

public class SVGEllipse extends SVGElement {
	
	public float x;
	public float y;
	public float width;
	public float height;
	
	public int resolution = 30;
	
	ArrayList<PVector> points;
	ArrayList<Line> lines;

	public SVGEllipse(Element e) {
		super(e);
		
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
	}
	
	@Override
	public void bake(GeometryFile geom,float hatchWidth) {
		lines = new ArrayList<Line>();
		points = new ArrayList<PVector>();
		for (int i = 0; i< resolution; i++) {
			float f = ((float)i / resolution) * Util.TWO_PI;
			PVector v = new PVector((float)(x + (Math.cos(f) * width)),(float)(y + (Math.sin(f) * height)));
			points.add(v);
		}
		
		Line l;
		for (int i = 0; i < resolution-1; i++) {
			l = new Line(points.get(i),points.get(i+1));
			l.color(strokeColor);
			if (stroked) geom.add(l);
			lines.add(l);
		}
		l = new Line(points.get(points.size()-1),points.get(0));
		l.color(strokeColor);
		if (stroked) geom.add(l);
		lines.add(l);
		
		if (!filled) return;
		
		for (int y = (int)(this.y - height); y < this.y + (height); y += hatchWidth) {
			ArrayList<PVector> intersections = new ArrayList<PVector>();
			// Find intersections
			for (Line line : lines) {
				float x = line.xIntersect(y);
				//System.out.println(x + " " + y + " " + line.startPoint + " " + line.endPoint);
				if (line.containsX(x)) {
					intersections.add(new PVector(x,y));
				}
			}
			// Sort intersections left to right.
			ArrayList<PVector> sortedIntersections = new ArrayList<PVector>();
			while(intersections.size() > 0) {
				float mostLeftValue = Float.MAX_VALUE;
				PVector mostLeftPoint = null;
				for (PVector v : intersections) {
					if (mostLeftPoint == null || v.x < mostLeftValue) {
						mostLeftPoint = v;
						mostLeftValue = v.x;
					}
				}
				sortedIntersections.add(mostLeftPoint);
				intersections.remove(mostLeftPoint);
			}
			// Create lines
			for (int i = 0 ; i < sortedIntersections.size() / 2; i++) {
				Line hatch = new Line(sortedIntersections.get(i * 2),sortedIntersections.get(i * 2 + 1));
				hatch.color(fillColor);
				geom.add(hatch);
			}	
		}
	}

	@Override
	public void plot(ArrayList<String> out, float hatchWidth) {
		float s = 279.4f / Module_Plotter.canvasHeight;
		
		PVector point = points.get(0);
		PVector lastPoint = point;
		float d = Module_Plotter.minimalLineDistance.value;
		
		out.add("LIN {X " + point.x * s + " ,Y " + point.y * s + " ,Z -10}");
		out.add("LIN {Z 0}");
		for (int i = 1; i < points.size(); i++) {
			point = points.get(i);
			if (lastPoint.dist(point) < d) continue;
			
			lastPoint = point;
			out.add("LIN {X " + point.x * s + " ,Y " + point.y * s + " ,Z 0}");
		}
		out.add("LIN {Z -10}");	
	}
}
