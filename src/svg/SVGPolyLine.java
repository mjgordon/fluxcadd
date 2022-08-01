package svg;

import java.util.ArrayList;

import org.joml.Vector3d;
import org.w3c.dom.Element;

import geometry.GeometryDatabase;
import geometry.Polyline;

public class SVGPolyLine extends SVGElement {

	private ArrayList<Vector3d> points;


	public SVGPolyLine(Element e) {
		super(e);
		points = new ArrayList<Vector3d>();
		String pathString = e.getAttribute("points");
		String[] pointStrings = pathString.split(" ");
		for (String s : pointStrings) {
			if (s.equals(""))
				continue;
			String[] parts = s.split(",");
			Vector3d v = new Vector3d(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), 0);
			points.add(v);
		}
	}


	@Override
	public void bake(GeometryDatabase geom) {
		Polyline polyline = new Polyline(points);
		polyline.setColor(strokeColor);
		geom.add(polyline);
	}
}
