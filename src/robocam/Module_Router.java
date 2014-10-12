package robocam;

import java.util.ArrayList;

import geometry.Box;
import geometry.Line;
import geometry.OBJModel;
import controller.*;
import ui.Content_View;
import ui.ViewType;
import ui.Content;
import utility.MutableFloat;
import utility.MutableInteger;
import utility.PVector;
import utility.Util;
import utility.Vector6;

public class Module_Router extends Module implements Controllable {

	OBJModel currentModel;

	Controller_DropDown modelDropDown;
	Controller_Button sliceRadialButton;
	Controller_Button sliceStackButton;
	Controller_TextField sliceAmountField;
	Controller_CheckBox boundingBoxCheckBox;
	Controller_TextField minimumVoxelField;
	
	boolean stackMostRecent = true;
	MutableInteger sliceAmount = new MutableInteger(200);
	MutableFloat minimumVoxelSize = new MutableFloat(1);
	
	public Box boundingBox;
	public Box octreeBox;
	
	public Module_Router(Content parent, Content_View associatedView) {
		super(parent, associatedView);
		
		associatedView.changeType(ViewType.PERSP);
		setupControl();
		
		currentModel = new OBJModel("wt_teapot.obj");
		currentModel.scale(3);
		boundingBox = new Box(currentModel.getBoundingBox());
		System.out.println(boundingBox.size);
		geometry.add("#bounding_box",boundingBox);
		
		octreeBox = new Box(getMaxVoxel());
		
		sliceStack(200);	
		
	}
	
	public void sliceStack(int slices) {
		if (currentModel == null) return;
		stackMostRecent = true;
		geometry.clear();
		
		//Find low and high extents;
		float low = Float.MAX_VALUE;
		float high = Float.MIN_VALUE;
		for (PVector v : currentModel.vertices) {
			if (v.z < low) low = v.z;
			if (v.z > high) high = v.z;
		}
		
		//Loop through each height
		for (int i = 0; i < slices; i++) {
			float h = low + (i * ((high-low) / slices));
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
		geometry.add(currentModel);
		geometry.add("#bounding_box",boundingBox);
		geometry.add(octreeBox);
	}
	
	public void sliceRadial(int slices) {
		if (currentModel == null) return;
		stackMostRecent = false;
		geometry.clear();
		//Loop through each slice
		for (int i = 0; i < slices; i++) {
			if ( i > 80) continue;
			float r = Util.TWO_PI / slices * i;
			//For each slice, create a list of lines from each polygon
			ArrayList<Line> lines = new ArrayList<Line>();
			for (OBJModel.Polygon polygon : currentModel.polygons) {
				ArrayList<Line> edges = polygon.getLines();
				ArrayList<PVector> intersects = new ArrayList<PVector>();
				for (Line line : edges) {
					PVector intersect = line.radialIntersect(r);
					if (intersect == null) continue;
					if (Math.abs(intersect.x) > 0.1 && Math.abs(intersect.y) > 0.1) {
						if (checkQuadrant(intersect,r) == false) continue;
					}
					else System.out.println(intersect);
					intersects.add(intersect);
				}
				if (intersects.size() == 2) {
					Line line = new Line(intersects.get(0),intersects.get(1));
					line.color(0, 0, 1);
					lines.add(line);
					geometry.add(line);
				}
			}
		}
		geometry.add(currentModel);
		geometry.add("#bounding_box",boundingBox);
		geometry.add(octreeBox);

	}
	
	public boolean checkQuadrant(PVector v, float r) {
		if (v == null) return(false);
		float r2 = (float)Math.atan2(v.y, v.x);
		float delta = Util.absoluteAngleDifference(r,r2);
		if (delta < Util.HALF_PI) return(true);
		else {
			return(false);
		}
	}
	
	public Vector6 getMaxVoxel() {
		Vector6 out = new Vector6();
		float f = minimumVoxelSize.value;
		while(f < boundingBox.size.x || f < boundingBox.size.y || f < boundingBox.size.z) {
			f *= 2;
		}
		out.x = out.y = out.z = f;
		return(out);
	}

	@Override
	public void setupControl() {
		controllerManager = new ControllerManager(this);	
		
		sliceRadialButton = new Controller_Button(controllerManager,"sliceRadial","Slice Radially",20,getHeight() - 100,20,20);
		controllerManager.add(sliceRadialButton);
		
		sliceStackButton = new Controller_Button(controllerManager,"sliceStack","Slice into Stack",150,getHeight() - 100,20,20);
		controllerManager.add(sliceStackButton);
		
		sliceAmountField = new Controller_TextField(controllerManager,"sliceAmount","Number of Slices",sliceAmount,20,getHeight() - 150,60,20);
		controllerManager.add(sliceAmountField);
		
		String[] options = {"Visible","Ghosted","Invisible"};
		modelDropDown = new Controller_DropDown(controllerManager,"modelDrop","OBJ Visiblity",20,getHeight() - 200,80,20,options);
		controllerManager.add(modelDropDown);
		
		boundingBoxCheckBox = new Controller_CheckBox(controllerManager,"boundingToggle","Show Bounding Box",20,20,20,20);
		controllerManager.add(boundingBoxCheckBox);
		
		minimumVoxelField = new Controller_TextField(controllerManager,"minimumVoxelSize","Minimum Voxel Size",minimumVoxelSize,20,80,60,20);
		controllerManager.add(minimumVoxelField);
	}

	@Override
	public void controllerEvent(String name) {
		if (name.equals("sliceRadial")) sliceRadial(200);
		else if (name.equals("sliceStack")) sliceStack(200);
		else if (name.equals("sliceAmount")) {
			if (stackMostRecent) sliceStack(sliceAmount.get());
			else sliceRadial(sliceAmount.get());
		}
		else if (name.equals("modelDrop")) {
			if (modelDropDown.selectedValue == 0) currentModel.graphicSetting = OBJModel.VISIBLE;
			else if (modelDropDown.selectedValue == 1) currentModel.graphicSetting = OBJModel.GHOSTED;
			else if (modelDropDown.selectedValue == 2) currentModel.graphicSetting = OBJModel.INVISIBLE;
		}
		else if (name.equals("boundingToggle")) {
			System.out.println(geometry.get("#bounding_box"));
			geometry.get("#bounding_box").visible = boundingBoxCheckBox.state;
		}
		
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
