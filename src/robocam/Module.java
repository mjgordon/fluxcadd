package robocam;

import java.util.ArrayList;

import lisp.GeometryFile;

import ui.Content_View;
import ui.Content;
import utility.PVector;
import utility.Vector6;
import controller.ControllerManager;

public abstract class Module {
	Content parent;
	
	ControllerManager controllerManager;
	
	GeometryFile geometry;
	
	Content_View associatedView;
	
	ArrayList<Vector6> toolPath;
	ArrayList<PVector> endPoints;
	
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
	
	public void poll() {
		controllerManager.poll();
	}
	
	public void keyPressed() {
		controllerManager.keyPressed();
	}
	
	public abstract void setupControl();
}
