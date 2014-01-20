package svg;

import java.util.ArrayList;

import lisp.GeometryFile;

import geometry.Line;

import org.w3c.dom.Element;

import robocam.Module_Plotter;

import utility.PVector;


public class SVGRect extends SVGElement {

	float x;
	float y;
	float width;
	float height;

	Line l1;
	Line l2;
	Line l3;
	Line l4;

	public ArrayList<Line> lines;

	public SVGRect(Element e) {
		super(e);

		x = Float.valueOf(e.getAttribute("x"));
		y = Float.valueOf(e.getAttribute("y"));
		width = Float.valueOf(e.getAttribute("width"));
		height = Float.valueOf(e.getAttribute("height"));
	}

	@Override
	public void bake(GeometryFile geom, float hatchWidth) {
		l1 = new Line(new PVector(x, y), new PVector(x + width, y));
		l1.color(strokeColor);
		if (stroked)
			geom.add(l1);

		l2 = new Line(new PVector(x + width, y), new PVector(x + width, y
				+ height));
		l2.color(strokeColor);
		if (stroked)
			geom.add(l2);

		l3 = new Line(new PVector(x + width, y + height), new PVector(x, y
				+ height));
		l3.color(strokeColor);
		if (stroked)
			geom.add(l3);

		l4 = new Line(new PVector(x, y + height), new PVector(x, y));
		l4.color(strokeColor);
		if (stroked)
			geom.add(l4);

		if (!filled)
			return;

		lines = new ArrayList<Line>();
		lines.add(l1);
		lines.add(l2);
		lines.add(l3);
		lines.add(l4);

		if (!filled)
			return;
		for (int y = (int) (this.y); y < (int) (this.y + height); y += hatchWidth) {
			ArrayList<PVector> intersections = new ArrayList<PVector>();
			// Find intersections
			for (Line line : lines) {
				float x = line.xIntersect(y);
				// System.out.println(x + " " + y + " " + line.startPoint + " "
				// + line.endPoint);
				if (line.containsX(x)) {
					intersections.add(new PVector(x, y));
				}
			}
			// Sort intersections left to right.
			ArrayList<PVector> sortedIntersections = new ArrayList<PVector>();
			while (intersections.size() > 0) {
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
			for (int i = 0; i < sortedIntersections.size() / 2; i++) {
				Line hatch = new Line(sortedIntersections.get(i * 2),
						sortedIntersections.get(i * 2 + 1));
				hatch.color(fillColor);
				geom.add(hatch);
			}
		}

	}

	@Override
	public void plot(ArrayList<String> out, float hatchWidth) {
		float s;
		if (Module_Plotter.canvasHeight < Module_Plotter.canvasWidth)
			s = 279.4f / Module_Plotter.canvasWidth;
		else
			s = 279.4f / Module_Plotter.canvasHeight;
		
		System.out.println(Module_Plotter.canvasWidth + " " + Module_Plotter.canvasHeight + " " + s);

		if (stroked) {
			out.add("LIN {X " + l1.startPoint.x * s + " ,Y " + l1.startPoint.y
					* s + " ,Z -10}");
			out.add("LIN {Z 0}");
			out.add("LIN {X " + l1.endPoint.x * s + " ,Y " + l1.endPoint.y * s
					+ "}");
			out.add("LIN {X " + l2.endPoint.x * s + " ,Y " + l2.endPoint.y * s
					+ "}");
			out.add("LIN {X " + l3.endPoint.x * s + " ,Y " + l3.endPoint.y * s
					+ "}");
			out.add("LIN {X " + l4.endPoint.x * s + " ,Y " + l4.endPoint.y * s
					+ "}");

			out.add("LIN {Z -10}");
		}

		if (filled) {
			for (int y = (int) (this.y); y < (int) (this.y + height); y += hatchWidth) {
				ArrayList<PVector> intersections = new ArrayList<PVector>();
				// Find intersections
				for (Line line : lines) {
					float x = line.xIntersect(y);
					// System.out.println(x + " " + y + " " + line.startPoint +
					// " " + line.endPoint);
					if (line.containsX(x)) {
						intersections.add(new PVector(x, y));
					}
				}
				// Sort intersections left to right.
				ArrayList<PVector> sortedIntersections = new ArrayList<PVector>();
				while (intersections.size() > 0) {
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
				for (int i = 0; i < sortedIntersections.size() / 2; i++) {
					PVector p1 = sortedIntersections.get(i * 2);
					PVector p2 = sortedIntersections.get(i * 2 + 1);
					out.add("LIN {Z -10}");
					out.add("LIN {X " + p1.x * s + ", Y " + p1.y * s + "}");
					out.add("LIN {Z 0}");
					out.add("LIN {X " + p2.x * s + ", Y " + p2.y * s + "}");
					out.add("LIN {Z -10}");
				}
			}
		}
	}
}
