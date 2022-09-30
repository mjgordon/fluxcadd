package robocam;

import geometry.GeometryDatabase;

import java.util.ArrayList;

import org.joml.Vector3d;

import ui.Content_View;
import ui.Content;
import utility.Vector6;
import controller.UIEControlManager;
import event.EventListener;

public abstract class Module implements EventListener {
	protected Content parent;

	protected UIEControlManager controllerManager;

	protected GeometryDatabase geometry;

	protected Content_View associatedView;

	protected ArrayList<Vector6> toolPath;
	protected ArrayList<Vector3d> endPoints;


	public Module(Content parent, Content_View associatedView) {
		this.parent = parent;
		this.associatedView = associatedView;
		controllerManager = new UIEControlManager(getWidth(), getHeight(), 10, 100, 10, 10);
		activate();
	}


	public void activate() {
		geometry = new GeometryDatabase();
		associatedView.geometry = geometry;

		toolPath = new ArrayList<Vector6>();
		endPoints = new ArrayList<Vector3d>();
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


	protected abstract void setupControl();


	public int getX() {
		return (parent.getX());
	}


	public int getY() {
		return (parent.getY());
	}


	public int getWidth() {
		return (parent.getWidth());
	}


	public int getHeight() {
		return (parent.getHeight());
	}
}
