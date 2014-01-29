package robocam;

import java.util.ArrayList;

import geometry.Line;
import geometry.OBJModel;
import controller.Controllable;
import controller.ControllerManager;
import ui.Content_View;
import ui.ViewType;
import ui.WindowContent;
import utility.PVector;

public class Module_Router extends Module implements Controllable {

	OBJModel currentModel;
	
	public Module_Router(WindowContent parent, Content_View associatedView) {
		super(parent, associatedView);
		
		associatedView.changeType(ViewType.PERSP);
		setupControl();
		
		currentModel = new OBJModel("sphere.obj");
		this.geometry.add(currentModel);
		
		slice(20);
		
	}

	
	public void slice(int layers) {
		if (currentModel == null) return;
		
		//Find low and high extents;
		float low = Float.MAX_VALUE;
		float high = Float.MIN_VALUE;
		for (PVector v : currentModel.vertexes) {
			if (v.z < low) low = v.z;
			if (v.z > high) high = v.z;
		}
		
		//Loop through each height
		for (int i = 0; i < layers; i++) {
			float h = low + (i * ((high-low) / 20));
			//For each height, create a list of lines from each polygon
			ArrayList<Line> lines = new ArrayList<Line>();
			for (OBJModel.Polygon polygon : currentModel.polygons) {
				ArrayList<Line> edges = polygon.getLines();
				ArrayList<PVector> intersects = new ArrayList<PVector>();
				for (Line line : edges) {
					PVector intersect = line.xyIntersect(h);
					if (intersect != null) intersects.add(intersect);
				}
				if (intersects.size() == 2) {
					Line line = new Line(intersects.get(0),intersects.get(1));
					line.color(0, 0, 1);
					lines.add(line);
					geometry.add(line);
				}
			}
			
		}
	}

	@Override
	public void setupControl() {
		controllerManager = new ControllerManager(this);	
	}

	@Override
	public void controllerEvent(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getX() {
		return(parent.getX());
	}

	@Override
	public int getY() {
		return(parent.getY());
	}

	@Override
	public int getWidth() {
		return(parent.getWidth());
	}

	@Override
	public int getHeight() {
		return(parent.getHeight());
	}

}
