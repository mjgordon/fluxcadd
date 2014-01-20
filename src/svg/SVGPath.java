package svg;

import geometry.Line;

import java.util.ArrayList;

import lisp.GeometryFile;

import org.w3c.dom.Element;

import robocam.Module_Plotter;

import utility.PVector;
import utility.Util;

/**
 * Still need to implement, Q,T,A
 * 
 */
public class SVGPath extends SVGElement {

	String d;
	public ArrayList<PVector> points;
	public ArrayList<Integer> subpaths;
	public ArrayList<Line> lines;

	public char currentCommand;
	public ArrayList<Float> currentNumbers;
	public String currentString = "";

	public static PVector currentPosition = new PVector(0, 0, 0);
	public static PVector currentControlPoint = new PVector();

	public int bezierResolution = 10;

	public PVector highestPoint = null;
	public PVector lowestPoint = null;

	public SVGPath(Element e) {
		super(e);

		d = e.getAttribute("d");

		currentCommand = 0;
		currentNumbers = new ArrayList<Float>();
		points = new ArrayList<PVector>();
		subpaths = new ArrayList<Integer>();

		for (char c : d.toCharArray()) {
			if (c == 'C' || c == 'c' || c == 'H' || c == 'h' || c == 'L'
					|| c == 'l' || c == 'M' || c == 'm' || c == 'S' || c == 's'
					|| c == 'V' || c == 'v') {
				if (currentCommand == 0) {
					currentCommand = c;
					continue;
				}
				completeNumber();
				completeCommand();
				currentCommand = c;
			} else if (c == '-') {
				if (currentString.length() > 0)
					completeNumber();
				currentString += c;
			} else if (c == ',') {
				completeNumber();
			} else if (c == ' ' || c == '\n' || c == 'z') {
				continue;
			} else {
				currentString += c;
			}
		}
		completeNumber();
		completeCommand();
	}

	public void completeNumber() {
		currentNumbers.add(Float.valueOf(currentString));
		currentString = "";
	}

	public void completeCommand() {
		if (currentCommand == 'C') {
			if (currentNumbers.size() > 6)
				System.out.println("Need to implement polybeziers on 'C'");
			else if (currentNumbers.size() < 6)
				System.out.println("Less than 6 commands on 'C'");
			PVector p1 = currentPosition.get();
			PVector p2 = new PVector(currentNumbers.get(4),
					currentNumbers.get(5));
			PVector cp1 = new PVector(currentNumbers.get(0),
					currentNumbers.get(1));
			PVector cp2 = new PVector(currentNumbers.get(2),
					currentNumbers.get(3));
			for (int i = 0; i < bezierResolution; i++) {
				float t = (float) i / bezierResolution;
				PVector point = evaluateBezier(p1, p2, cp1, cp2, t);
				currentPosition = point;
				addPoint();
			}
			currentPosition = p2;
			currentControlPoint = cp2;
		} else if (currentCommand == 'c') {
			if (currentNumbers.size() > 6)
				System.out.println("Need to implement polybeziers on 'c'");
			else if (currentNumbers.size() < 6)
				System.out.println("Less than 6 commands on 'c'");

			PVector p1 = currentPosition.get();
			PVector p2 = new PVector(currentNumbers.get(4),
					currentNumbers.get(5));
			p2.add(currentPosition);
			PVector cp1 = new PVector(currentNumbers.get(0),
					currentNumbers.get(1));
			cp1.add(currentPosition);
			PVector cp2 = new PVector(currentNumbers.get(2),
					currentNumbers.get(3));
			cp2.add(currentPosition);
			for (int i = 0; i < bezierResolution; i++) {
				float t = (float) i / bezierResolution;
				PVector point = evaluateBezier(p1, p2, cp1, cp2, t);
				currentPosition = point;
				addPoint();
			}
			currentPosition = p2;
			currentControlPoint = cp2;
		} else if (currentCommand == 'H') {
			for (Float f : currentNumbers) {
				currentPosition = new PVector(f, currentPosition.y);
				addPoint();
			}
		} else if (currentCommand == 'h') {
			for (Float f : currentNumbers) {
				currentPosition = new PVector(currentPosition.x + f,
						currentPosition.y);
				addPoint();
			}
		} else if (currentCommand == 'L') {
			for (int i = 0; i < currentNumbers.size() / 2; i++) {
				currentPosition = new PVector(currentNumbers.get(i * 2),
						currentNumbers.get((i * 2) + 1));
				addPoint();
			}
		} else if (currentCommand == 'l') {
			for (int i = 0; i < currentNumbers.size() / 2; i++) {
				currentPosition.x += currentNumbers.get(i * 2);
				currentPosition.y += currentNumbers.get((i * 2) + 1);
				addPoint();
			}
		} else if (currentCommand == 'M') {
			if (currentNumbers.size() > 2)
				System.out
						.println("Need to implement implicit lineto's on 'M'");
			else if (currentNumbers.size() < 2)
				System.out.println("Less than 2 commands on M");
			currentPosition = new PVector(currentNumbers.get(0),
					currentNumbers.get(1));
			addPoint();
			subpaths.add(points.size());
		} else if (currentCommand == 'm') {
			if (currentNumbers.size() > 2) {
				System.out
						.println("Need to implement implicit lineto's on 'm'");
			} else if (currentNumbers.size() < 2) {
				System.out.println("Less than two commands on m");
			}
			currentPosition.add(new PVector(currentNumbers.get(0),
					currentNumbers.get(1)));
			addPoint();
			subpaths.add(points.size());
		} else if (currentCommand == 'S') {
			if (currentNumbers.size() > 4)
				System.out.println("Need to implement polybeziers in S");
			else if (currentNumbers.size() < 4)
				System.out.println("Less than 4 commands on S");

			PVector p1 = currentPosition.get();
			PVector p2 = new PVector(currentNumbers.get(2),
					currentNumbers.get(3));
			PVector cp1 = currentPosition.get();
			PVector temp = PVector.sub(currentControlPoint, currentPosition);
			cp1.add(PVector.mult(temp, -1));
			PVector cp2 = new PVector(currentNumbers.get(0),
					currentNumbers.get(1));
			for (int i = 0; i < bezierResolution; i++) {
				float t = (float) i / bezierResolution;
				PVector point = evaluateBezier(p1, p2, cp1, cp2, t);
				currentPosition = point;
				addPoint();
			}
			currentPosition = p2;
			currentControlPoint = cp2;

		}

		else if (currentCommand == 's') {
			if (currentNumbers.size() > 4)
				System.out.println("Need to implement polybeziers in s");
			else if (currentNumbers.size() < 4)
				System.out.println("Less than 4 commands on s");
			PVector p1 = currentPosition.get();
			PVector p2 = new PVector(currentNumbers.get(2),
					currentNumbers.get(3));
			p2.add(currentPosition);
			PVector cp1 = currentPosition.get();
			PVector temp = PVector.sub(currentControlPoint, currentPosition);
			cp1.add(PVector.mult(temp, -1));
			PVector cp2 = new PVector(currentNumbers.get(0),
					currentNumbers.get(1));
			cp2.add(currentPosition);
			for (int i = 0; i < bezierResolution; i++) {
				float t = (float) i / bezierResolution;
				PVector point = evaluateBezier(p1, p2, cp1, cp2, t);
				currentPosition = point;
				addPoint();
			}
			currentPosition = p2;
			currentControlPoint = cp2;
		}

		else if (currentCommand == 'V') {
			for (Float f : currentNumbers) {
				currentPosition = new PVector(currentPosition.x, f);
				addPoint();
			}
		} else if (currentCommand == 'v') {
			for (Float f : currentNumbers) {
				currentPosition = new PVector(currentPosition.x,
						currentPosition.y + f);
				addPoint();
			}
		}

		else
			System.out.println("Need to implement '" + currentCommand
					+ "' command.");

		currentNumbers = new ArrayList<Float>();
	}

	public void addPoint() {
		PVector v = currentPosition.get();
		points.add(v);

		if (highestPoint == null || v.y < highestPoint.y)
			highestPoint = v;
		if (lowestPoint == null || v.y > lowestPoint.y)
			lowestPoint = v;

	}

	@Override
	public void bake(GeometryFile geom, float hatchWidth) {
		lines = new ArrayList<Line>();
		for (int i = 0; i < points.size() - 1; i++) {
			if (i != 0 && subpaths.contains(i + 2)) {
				continue;
			}
			Line l = new Line(points.get(i), points.get(i + 1));
			l.color(strokeColor);
			if (stroked)
				geom.add(l);
			lines.add(l);
		}

		lines.add(new Line(points.get(points.size() - 1), points.get(0)));

		if (!filled)
			return;
		for (int y = (int) highestPoint.y; y < lowestPoint.y; y += hatchWidth) {
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

		PVector point = points.get(0);
		PVector lastPoint = point;
		float d = Module_Plotter.minimalLineDistance.value;
		if (stroked) {
			out.add("LIN {X " + point.x * s + " ,Y " + point.y * s + " ,Z -10}");
			out.add("LIN {Z 0}");
			for (int i = 1; i < points.size(); i++) {
				point = points.get(i);
				if (lastPoint.dist(point) < d)
					continue;

				lastPoint = point;
				out.add("LIN {X " + point.x * s + " ,Y " + point.y * s
						+ " ,Z 0}");
			}
			out.add("LIN {Z -10}");
		}

		if (filled) {
			for (int y = (int) highestPoint.y; y < lowestPoint.y; y += hatchWidth) {
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

	public PVector evaluateBezier(PVector p1, PVector p2, PVector cp1,
			PVector cp2, float t) {
		PVector mid1 = new PVector(Util.lerp(p1.x, cp1.x, t), Util.lerp(p1.y,
				cp1.y, t));
		PVector mid2 = new PVector(Util.lerp(cp1.x, cp2.x, t), Util.lerp(cp1.y,
				cp2.y, t));
		PVector mid3 = new PVector(Util.lerp(cp2.x, p2.x, t), Util.lerp(cp2.y,
				p2.y, t));

		PVector mid4 = new PVector(Util.lerp(mid1.x, mid2.x, t), Util.lerp(
				mid1.y, mid2.y, t));
		PVector mid5 = new PVector(Util.lerp(mid2.x, mid3.x, t), Util.lerp(
				mid2.y, mid3.y, t));

		return (new PVector(Util.lerp(mid4.x, mid5.x, t), Util.lerp(mid4.y,
				mid5.y, t)));
	}

}
