package svg;

import geometry.Curve;
import geometry.GeometryFile;
import geometry.Group;
import geometry.Line;

import java.util.ArrayList;

import org.w3c.dom.Element;

import utility.PVector;
import utility.Util;

/**
 * Still need to implement, Q,T,A,Z,proper subpaths
 * 
 */
public class SVGPath extends SVGElement {


	public ArrayList<Line> lines;

	private String d;

	private char currentCommand;
	private ArrayList<Float> currentNumbers;
	private String currentString = "";

	private PVector currentPosition = new PVector(0, 0, 0);
	private PVector currentControlPoint = new PVector();

	private int bezierResolution = 10;

	private PVector highestPoint = null;
	private PVector lowestPoint = null;
	
	private Group group;

	private char[] commandChars = { 'C', 'c', 'H', 'h', 'L', 'l', 'M', 'm', 'S', 's', 'V', 'v' };

	public SVGPath(Element e) {
		super(e);

		d = e.getAttribute("d");
	}

	@Override
	public void bake(GeometryFile geom) {
		group = new Group();

		currentCommand = 0;
		currentNumbers = new ArrayList<Float>();

		for (char c : d.toCharArray()) {
			if (Util.arrayContainsChar(commandChars, c)) {
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
		
		
		geom.add(group);

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
			PVector p1 = currentPosition.copy();
			PVector p2 = new PVector(currentNumbers.get(4), currentNumbers.get(5));
			PVector cp1 = new PVector(currentNumbers.get(0), currentNumbers.get(1));
			PVector cp2 = new PVector(currentNumbers.get(2), currentNumbers.get(3));
			group.add(new Curve(p1,p2,cp1,cp2));
			currentPosition = p2;
			currentControlPoint = cp2;
		} 
		else if (currentCommand == 'c') {
			if (currentNumbers.size() > 6)
				System.out.println("Need to implement polybeziers on 'c'");
			else if (currentNumbers.size() < 6)
				System.out.println("Less than 6 commands on 'c'");

			PVector p1 = currentPosition.copy();
			PVector p2 = new PVector(currentNumbers.get(4), currentNumbers.get(5));
			p2.add(currentPosition);
			PVector cp1 = new PVector(currentNumbers.get(0), currentNumbers.get(1));
			cp1.add(currentPosition);
			PVector cp2 = new PVector(currentNumbers.get(2), currentNumbers.get(3));
			cp2.add(currentPosition);
			group.add(new Curve(p1,p2,cp1,cp2));
			currentPosition = p2;
			currentControlPoint = cp2;
		} 
		else if (currentCommand == 'H') {
			for (Float f : currentNumbers) {
				PVector newPosition = new PVector(f,currentPosition.y);
				group.add(new Line(currentPosition,newPosition));
				currentPosition = newPosition;
			}
		} 
		else if (currentCommand == 'h') {
			for (Float f : currentNumbers) {
				PVector newPosition = new PVector(currentPosition.x + f,currentPosition.y);
				group.add(new Line(currentPosition,newPosition));
				currentPosition = newPosition;
			}
		} 
		else if (currentCommand == 'L') {
			for (int i = 0; i < currentNumbers.size() / 2; i++) {
				PVector newPosition = new PVector(currentNumbers.get(i * 2),currentNumbers.get((i * 2) + 1));
				group.add(new Line(currentPosition,newPosition));
				currentPosition = newPosition;
			}
		} 
		else if (currentCommand == 'l') {
			for (int i = 0; i < currentNumbers.size() / 2; i++) {
				PVector newPosition = new PVector();
				newPosition.x = currentPosition.x + currentNumbers.get(i * 2);
				newPosition.y = currentPosition.y + currentNumbers.get((i * 2) + 1);
				group.add(new Line(currentPosition,newPosition));
				currentPosition = newPosition;
			}
		} 
		else if (currentCommand == 'M') {
			if (currentNumbers.size() > 2)
				System.out.println("Need to implement implicit lineto's on 'M'");
			else if (currentNumbers.size() < 2)
				System.out.println("Less than 2 commands on M");
			currentPosition = new PVector(currentNumbers.get(0), currentNumbers.get(1));
		
		} 
		else if (currentCommand == 'm') {
			if (currentNumbers.size() > 2) {
				System.out.println("Need to implement implicit lineto's on 'm'");
			} else if (currentNumbers.size() < 2) {
				System.out.println("Less than two commands on m");
			}
			currentPosition.add(new PVector(currentNumbers.get(0), currentNumbers.get(1)));
		} 
		else if (currentCommand == 'S') {
			if (currentNumbers.size() > 4)
				System.out.println("Need to implement polybeziers in S");
			else if (currentNumbers.size() < 4)
				System.out.println("Less than 4 commands on S");

			PVector p1 = currentPosition.copy();
			PVector p2 = new PVector(currentNumbers.get(2), currentNumbers.get(3));
			PVector cp1 = currentPosition.copy();
			PVector temp = PVector.sub(currentControlPoint, currentPosition);
			cp1.add(PVector.mult(temp, -1));
			PVector cp2 = new PVector(currentNumbers.get(0), currentNumbers.get(1));
			group.add(new Curve(p1,p2,cp1,cp2));
			currentPosition = p2;
			currentControlPoint = cp2;
		}
		else if (currentCommand == 's') {
			if (currentNumbers.size() > 4)
				System.out.println("Need to implement polybeziers in s");
			else if (currentNumbers.size() < 4)
				System.out.println("Less than 4 commands on s");
			PVector p1 = currentPosition.copy();
			PVector p2 = new PVector(currentNumbers.get(2), currentNumbers.get(3));
			p2.add(currentPosition);
			PVector cp1 = currentPosition.copy();
			PVector temp = PVector.sub(currentControlPoint, currentPosition);
			cp1.add(PVector.mult(temp, -1));
			PVector cp2 = new PVector(currentNumbers.get(0), currentNumbers.get(1));
			cp2.add(currentPosition);
			group.add(new Curve(p1,p2,cp1,cp2));
			currentPosition = p2;
			currentControlPoint = cp2;
		}

		else if (currentCommand == 'V') {
			for (Float f : currentNumbers) {
				PVector newPosition = new PVector(currentPosition.x,f);
				group.add(new Line(currentPosition,newPosition));
				currentPosition = newPosition; 
			}
		} else if (currentCommand == 'v') {
			for (Float f : currentNumbers) {
				PVector newPosition = new PVector(currentPosition.x,currentPosition.y + f);
				group.add(new Line(currentPosition,newPosition));
				currentPosition = newPosition;
			}
		}

		else
			System.out.println("Need to implement '" + currentCommand + "' command.");

		currentNumbers = new ArrayList<Float>();
	}



	

}
