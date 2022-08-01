package svg;

import java.util.ArrayList;

import org.joml.Vector3d;
import org.w3c.dom.Element;

import geometry.Bezier;
import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;

import utility.Util;

/**
 * Still need to implement, Q,T,A,Z,proper subpaths
 * 
 */
public class SVGPath extends SVGElement {

	private String d;

	private char currentCommand;
	private ArrayList<Double> currentNumbers;
	private String currentNumberString = "";
	private String currentExponentString = "";
	private boolean usingExponent = false;

	private Vector3d currentPosition = new Vector3d(0, 0, 0);
	private Vector3d currentControlPoint = new Vector3d();

	private Group group;

	private char[] commandChars = { 'C', 'c', 'H', 'h', 'L', 'l', 'M', 'm', 'S', 's', 'V', 'v' };


	public SVGPath(Element e) {
		super(e);

		d = e.getAttribute("d");
	}


	@Override
	public void bake(GeometryDatabase geom) {
		group = new Group();

		currentCommand = 0;
		currentNumbers = new ArrayList<Double>();

		for (char c : d.toCharArray()) {
			if (Util.arrayContainsChar(commandChars, c)) {
				if (currentCommand == 0) {
					currentCommand = c;
					continue;
				}
				completeNumber();
				completeCommand();
				currentCommand = c;
			}
			else if (c == 'e') {
				usingExponent = true;
				addCharToString(c);
			}
			else if (c == '-') {
				if (usingExponent && currentExponentString.length() > 0 || !usingExponent && currentNumberString.length() > 0)
					completeNumber();
				addCharToString(c);
			}
			else if (c == ',' || c == ' ') {
				completeNumber();
			}
			else if (c == '\n' || c == 'z' || c == 'Z') {
				continue;
			}
			else {
				addCharToString(c);
			}
		}
		completeNumber();
		completeCommand();

		geom.add(group);
	}


	private void addCharToString(char c) {
//		if (usingExponent) {
//			currentExponentString += c;
//		}
//		else {
//			currentNumberString += c;
//		}
		currentNumberString += c;
	}


	private void completeNumber() {
		if (currentNumberString == "")
			return;
		double value = Double.valueOf(currentNumberString);
//		if (usingExponent) {
//			int exponent = Integer.valueOf(currentExponentString);
//			System.out.println(value + " : " + exponent);
//			value = (float) Math.pow(value, exponent);
//			System.out.println("Exponentiated value : " + value);
//		}
		currentNumbers.add(value);
		currentNumberString = "";
		currentExponentString = "";
		usingExponent = false;
	}


	private void completeCommand() {
		if (currentCommand == 'C') {
			commandCurveAbsolute();
		}
		else if (currentCommand == 'c') {
			commandCurveRelative();
		}
		else if (currentCommand == 'H') {
			for (Double d : currentNumbers) {
				Vector3d newPosition = new Vector3d(d, currentPosition.y, 0);
				group.add(new Line(currentPosition, newPosition).setColor(strokeColor));
				currentPosition = newPosition;
			}
		}
		else if (currentCommand == 'h') {
			for (Double d : currentNumbers) {
				Vector3d newPosition = new Vector3d(currentPosition.x + d, currentPosition.y, 0);
				group.add(new Line(currentPosition, newPosition).setColor(strokeColor));
				currentPosition = newPosition;
			}
		}
		else if (currentCommand == 'L') {
			for (int i = 0; i < currentNumbers.size() / 2; i++) {
				Vector3d newPosition = new Vector3d(currentNumbers.get(i * 2), currentNumbers.get((i * 2) + 1), 0);
				group.add(new Line(currentPosition, newPosition).setColor(strokeColor));
				currentPosition = newPosition;
			}
		}
		else if (currentCommand == 'l') {
			for (int i = 0; i < currentNumbers.size() / 2; i++) {
				Vector3d newPosition = new Vector3d();
				newPosition.x = currentPosition.x + currentNumbers.get(i * 2);
				newPosition.y = currentPosition.y + currentNumbers.get((i * 2) + 1);
				group.add(new Line(currentPosition, newPosition).setColor(strokeColor));
				currentPosition = newPosition;
			}
		}
		else if (currentCommand == 'M') {
			if (currentNumbers.size() % 2 != 0) {
				System.out.println(currentNumbers.size() + " commands passed to M");
				return;
			}
			for (int i = 0; i < currentNumbers.size(); i += 2) {
				Vector3d newPosition = new Vector3d();
				newPosition.x = currentNumbers.get(0 + i);
				newPosition.y = currentNumbers.get(1 + i);
				if (i >= 2) {
					group.add(new Line(currentPosition, newPosition).setColor(strokeColor));
				}
				currentPosition = newPosition;
			}

		}
		else if (currentCommand == 'm') {
			if (currentNumbers.size() % 2 != 0) {
				System.out.println(currentNumbers.size() + " commands passed to m");
				return;
			}

			for (int i = 0; i < currentNumbers.size(); i += 2) {
				Vector3d newPosition = new Vector3d();
				newPosition.x = currentPosition.x + currentNumbers.get(0 + i);
				newPosition.y = currentPosition.y + currentNumbers.get(1 + i);
				if (i >= 2) {
					group.add(new Line(currentPosition, newPosition).setColor(strokeColor));
				}
				currentPosition = newPosition;
			}

		}
		else if (currentCommand == 'S') {
			commandSmoothCurveAbsolute();
		}
		else if (currentCommand == 's') {
			commandSmoothCurveRelative();
		}
		else if (currentCommand == 'V') {
			for (Double d : currentNumbers) {
				Vector3d newPosition = new Vector3d(currentPosition.x, d, 0);
				group.add(new Line(currentPosition, newPosition).setColor(strokeColor));
				currentPosition = newPosition;
			}
		}
		else if (currentCommand == 'v') {
			for (Double d : currentNumbers) {
				Vector3d newPosition = new Vector3d(currentPosition.x, currentPosition.y + d, 0);
				group.add(new Line(currentPosition, newPosition).setColor(strokeColor));
				currentPosition = newPosition;
			}
		}

		else
			System.out.println("Need to implement '" + currentCommand + "' command.");

		currentNumbers = new ArrayList<Double>();
	}


	private void commandCurveAbsolute() {
		if (currentNumbers.size() % 6 != 0) {
			System.out.println("Less than 6 commands on 'C'");
			return;
		}

		for (int i = 0; i < currentNumbers.size(); i += 6) {
			Vector3d p1 = new Vector3d(currentPosition);
			Vector3d p2 = new Vector3d(currentNumbers.get(4 + i), currentNumbers.get(5 + i), 0);
			Vector3d cp1 = new Vector3d(currentNumbers.get(0 + i), currentNumbers.get(1 + i), 0);
			Vector3d cp2 = new Vector3d(currentNumbers.get(2 + i), currentNumbers.get(3 + i), 0);
			group.add(new Bezier(p1, p2, cp1, cp2).setColor(strokeColor));
			currentPosition = p2;
			currentControlPoint = cp2;
		}
	}


	private void commandCurveRelative() {
		if (currentNumbers.size() % 6 != 0) {
			System.out.println(currentNumbers.size() + " commands given to 'c'");
			System.out.println(currentNumbers.get(currentNumbers.size() - 1));
			for (Double d : currentNumbers) {
				System.out.println("   : " + d);
			}
			return;
		}
		for (int i = 0; i < currentNumbers.size(); i += 6) {
			Vector3d p1 = new Vector3d(currentPosition);
			Vector3d p2 = new Vector3d(currentNumbers.get(4 + i), currentNumbers.get(5 + i), 0);
			p2.add(currentPosition);
			Vector3d cp1 = new Vector3d(currentNumbers.get(0 + i), currentNumbers.get(1 + i), 0);
			cp1.add(currentPosition);
			Vector3d cp2 = new Vector3d(currentNumbers.get(2 + i), currentNumbers.get(3 + i), 0);
			cp2.add(currentPosition);
			group.add(new Bezier(p1, p2, cp1, cp2).setColor(strokeColor));
			currentPosition = p2;
			currentControlPoint = cp2;
		}
	}


	private void commandSmoothCurveAbsolute() {
		if (currentNumbers.size() % 4 != 0) {
			System.out.println("Less than 4 commands on 'S'");
			return;
		}

		for (int i = 0; i < currentNumbers.size(); i += 4) {
			Vector3d p1 = new Vector3d(currentPosition);
			Vector3d p2 = new Vector3d(currentNumbers.get(2), currentNumbers.get(3), 0);
			Vector3d cp1 = new Vector3d(currentPosition);
			cp1.add(new Vector3d(currentControlPoint).sub(currentPosition).mul(-1));
			Vector3d cp2 = new Vector3d(currentNumbers.get(0), currentNumbers.get(1), 0);
			group.add(new Bezier(p1, p2, cp1, cp2).setColor(strokeColor));
			currentPosition = p2;
			currentControlPoint = cp2;
		}
	}


	private void commandSmoothCurveRelative() {
		if (currentNumbers.size() % 4 != 0) {
			System.out.println("Less than 4 commands on 's'");
			return;
		}

		for (int i = 0; i < currentNumbers.size(); i += 4) {
			Vector3d p1 = new Vector3d(currentPosition);
			Vector3d p2 = new Vector3d(currentNumbers.get(2), currentNumbers.get(3), 0);
			p2.add(currentPosition);
			Vector3d cp1 = new Vector3d(currentPosition);
			cp1.add(new Vector3d(currentControlPoint).sub(currentPosition).mul(-1));
			Vector3d cp2 = new Vector3d(currentNumbers.get(0), currentNumbers.get(1), 0);
			cp2.add(currentPosition);
			group.add(new Bezier(p1, p2, cp1, cp2).setColor(strokeColor));
			currentPosition = p2;
			currentControlPoint = cp2;
		}
	}
}
