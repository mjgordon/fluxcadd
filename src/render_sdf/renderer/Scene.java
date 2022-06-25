package render_sdf.renderer;

import utility.PVectorD;

public class Scene {

	public Camera camera;

	public PVectorD sunPosition = new PVectorD(100, 100, 100);
	public double ambientLight = 0.2f;
	
	public int seed = 1;

	public Scene(int width, int height) {
		// Was this important?
		//p.randomSeed(seed);
		camera = new Camera(width,height);
	}
}
