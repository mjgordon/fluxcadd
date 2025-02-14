package render_sdf.renderer;

import org.joml.Vector3d;

import render_sdf.animation.Matrix4dAnimated;
import utility.Color3i;

public class Scene {
	
	public String name = null;

	public Camera camera;

	public Matrix4dAnimated sunPosition = new Matrix4dAnimated(new Vector3d(100, 100, 100), "Sun");
	public double ambientLight = 0.2f;
	public Color3i skyColor = new Color3i(40,100,255);
	
	public int frameStart = 0;
	public int frameEnd = 100;


	public Scene(int width, int height) {
		camera = new Camera(width, height);
	}
	
	public void setSkyColor(Color3i c) {
		this.skyColor = c;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
