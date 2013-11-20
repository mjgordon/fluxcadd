package svg;

import geometry.Line;

import java.util.ArrayList;

import org.w3c.dom.Element;

import robocam.Module_Plotter;

import data.GeometryFile;

import utility.PVector;

public class SVGPolyLine extends SVGElement {
	ArrayList<PVector> points;
	ArrayList<Line> lines;
	
	public PVector highestPoint = null;
	public PVector lowestPoint = null;

	public SVGPolyLine(Element e) {
	
		super(e);
		
		points = new ArrayList<PVector>();
		String pathString = e.getAttribute("points");
		String[] pointStrings = pathString.split(" ");
		for (String s : pointStrings) {
			if (s.equals("")) continue;
			String[] parts = s.split(",");
			PVector v = new PVector(Float.parseFloat(parts[0]),Float.parseFloat(parts[1]));
			if (highestPoint == null || v.y < highestPoint.y) highestPoint = v;
			if (lowestPoint == null || v.y > lowestPoint.y) lowestPoint = v;
			points.add(v);
		}
	}

	@Override
	public void bake(GeometryFile geom,float hatchWidth) {
		lines = new ArrayList<Line>();
		for (int i = 0; i < points.size()-1; i++) {
			PVector p1 = points.get(i);
			PVector p2 = points.get(i+1);
			
			Line l = new Line(p1,p2);
			l.color(strokeColor);
			if (stroked) geom.add(l);
			lines.add(l);
		}
		lines.add(new Line(points.get(points.size()-1),points.get(0)));
		
		if (!filled) return;
		for (int y = (int)highestPoint.y; y < lowestPoint.y; y += hatchWidth) {
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
		
		out.add("LIN {X " + point.x * s + " ,Y " + point.y * s + " ,Z -10}");
		out.add("LIN {Z 0}");
		for (int i = 1; i < points.size(); i++) {
			point = points.get(i);
			
			out.add("LIN {X " + point.x * s + " ,Y " + point.y * s + " ,Z 0}");
		}
		out.add("LIN {Z -10}");
		
	}
}
