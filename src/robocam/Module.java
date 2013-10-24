package robocam;

import ui.WindowContent;
import controller.ControllerManager;
import data.GeometryFile;

public abstract class Module {
	WindowContent parent;
	
	ControllerManager controllerManager;
	
	GeometryFile geometry;
	
	public void render() {
		controllerManager.render();
	}
	
	public void poll() {
		controllerManager.poll();
	}
	
	public void keyPressed() {
		controllerManager.keyPressed();
	}
}
