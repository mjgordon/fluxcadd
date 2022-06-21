package render_sdf.raytracer;

import java.util.ArrayList;

import render_sdf.renderer.Scene;
import utility.Color;
import utility.Util;
import utility.VectorD;

public class SceneRaytrace extends Scene {
	public ArrayList<Geometry> geometryList;


	public SceneRaytrace(int width, int height) {
		super(width,height);

		geometryList = new ArrayList<Geometry>();

		geometryList.add(new Sphere(new VectorD(0, 0, 0), 10, new Color(0xFFFF0000)));

		for (int i = 0; i < 10; i++) {
			geometryList.add(new Sphere(new VectorD(Util.random(-30, 30), Util.random(-30, 30), Util.random(4, 30)), Util.random(2, 8),
					new Color(Util.random(100, 255), Util.random(100, 255), Util.random(100, 255))));
		}

		geometryList.add(new GroundPlane(0));
	}
}
