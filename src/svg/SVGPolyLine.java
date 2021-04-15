package svg;

import java.util.ArrayList;

import org.w3c.dom.Element;

import geometry.GeometryDatabase;
import geometry.Polyline;
import utility.PVector;

public class SVGPolyLine extends SVGElement {

	private ArrayList<PVector> points;
	
	public SVGPolyLine(Element e) {
		super(e);
		points = new ArrayList<PVector>();
		String pathString = e.getAttribute("points");
		String[] pointStrings = pathString.split(" ");
		for (String s : pointStrings) {
			if (s.equals(""))
				continue;
			String[] parts = s.split(",");
			PVector v = new PVector(Float.parseFloat(parts[0]),
					Float.parseFloat(parts[1]));
			points.add(v);
		}
	}

	@Override
	public void bake(GeometryDatabase geom) {
		Polyline polyline = Polyline.fromVectors(points);
		polyline.setColor(strokeColor);
		geom.add(polyline);
	}
}
