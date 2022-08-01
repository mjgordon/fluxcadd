package render_sdf.renderer;

import org.joml.Vector3d;

public class Scene {

	public Camera camera;

	public Vector3d sunPosition = new Vector3d(100, 100, 100);
	public double ambientLight = 0.2f;


	public Scene(int width, int height) {
		camera = new Camera(width, height);
	}
}
