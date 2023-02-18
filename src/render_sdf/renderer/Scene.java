package render_sdf.renderer;

import org.joml.Vector3d;

import utility.Color;

public class Scene {
	
	public String name = null;

	public Camera camera;

	public Vector3d sunPosition = new Vector3d(100, 100, 100);
	public double ambientLight = 0.2f;
	public Color skyColor = new Color(40,100,255);


	public Scene(int width, int height) {
		camera = new Camera(width, height);
	}
	
	public void setSkyColor(Color c) {
		this.skyColor = c;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
