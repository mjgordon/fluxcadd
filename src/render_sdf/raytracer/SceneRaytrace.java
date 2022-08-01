package render_sdf.raytracer;

import java.util.ArrayList;

import org.joml.Vector3d;

import render_sdf.renderer.Scene;
import utility.Color;
import utility.Util;

public class SceneRaytrace extends Scene {
	public ArrayList<RenderGeometry> geometryList;


	public SceneRaytrace(int width, int height) {
		super(width, height);

		geometryList = new ArrayList<RenderGeometry>();

		geometryList.add(new Sphere(new Vector3d(0, 0, 0), 10, new Color(0xFFFF0000)));

		for (int i = 0; i < 10; i++) {
			geometryList.add(new Sphere(new Vector3d(Util.random(-30, 30), Util.random(-30, 30), Util.random(4, 30)), Util.random(2, 8),
					new Color(Util.random(100, 255), Util.random(100, 255), Util.random(100, 255))));
		}

		geometryList.add(new GroundPlane(0));
	}
}
