package robocam;

import geometry.GeometryFile;

import java.util.ArrayList;

import ui.Content_View;
import ui.Content;
import utility.PVector;
import utility.Vector6;
import controller.ControllerManager;

public abstract class Module {
	protected Content parent;
	
	protected ControllerManager controllerManager;
	
	protected GeometryFile geometry;
	
	protected Content_View associatedView;
	
	protected ArrayList<Vector6> toolPath;
	protected ArrayList<PVector> endPoints;
	
	public Module(Content parent, Content_View associatedView) {
		this.parent = parent;
		this.associatedView = associatedView;
		activate();
	}
	
	public void activate() {
		geometry = new GeometryFile();
		associatedView.geometry = geometry;
		
		toolPath = new ArrayList<Vector6>();
		endPoints = new ArrayList<PVector>();
	}
	
	public void render() {
		controllerManager.render();
	}
	
	public void poll(int mouseX, int mouseY) {
		controllerManager.poll(mouseX, mouseY);
	}
	
	public void keyPressed(int key) {
		controllerManager.keyPressed(key);
	}
	
	public void textInput(char character) {
		controllerManager.textInput(character);
	}
	
	public abstract void setupControl();
}
