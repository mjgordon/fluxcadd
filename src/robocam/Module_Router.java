package robocam;

import java.util.ArrayList;

import org.joml.Vector3d;

import geometry.Box;
import geometry.Line;
import geometry.Mesh;
import iofile.MeshOBJ;
import controller.*;
import event.EventMessage;
import ui.Content_View;
import ui.ViewType;
import ui.Content;
//import utility.MutableFloat;
import utility.MutableInteger;
import utility.Util;
import utility.math.UtilMath;

public class Module_Router extends Module {

	private Mesh currentModel;

	private UIEDropdown modelDropDown;
	private UIEButton sliceRadialButton;
	private UIEButton sliceStackButton;
	// private UIETextField sliceAmountField;
	private UIEToggle boundingBoxCheckBox;
	private UIETextField minimumVoxelField;

	private boolean stackMostRecent = true;
	private MutableInteger sliceAmount = new MutableInteger(200);
	// private MutableFloat minimumVoxelSize = new MutableFloat(1);

	private Box boundingBox;
	// private Box octreeBox;

	private int currentSlices = 200;;


	public Module_Router(Content parent, Content_View associatedView) {
		super(parent, associatedView);

		associatedView.changeType(ViewType.PERSP);
		setupControl();

		currentModel = MeshOBJ.loadMeshFromFile("demo_data/teapot.obj");
		boundingBox = currentModel.getBoundingBox();
		scaleModelTo(100);
		boundingBox = currentModel.getBoundingBox();
		boundingBox.name = "#bounding_box";
		geometry.add(boundingBox);

		// octreeBox = new Box(getMaxVoxel());
		// octreeBox.name = "#octree_box";

		sliceStack(currentSlices);

	}


	public void sliceStack(int slices) {
		if (currentModel == null)
			return;
		stackMostRecent = true;
		geometry.clear();

		// Find low and high extents;
		double low = Float.MAX_VALUE;
		double high = Float.MIN_VALUE;
		for (Vector3d v : currentModel.vertices) {
			if (v.z < low)
				low = v.z;
			if (v.z > high)
				high = v.z;
		}

		// Loop through each height
		for (int i = 0; i < slices; i++) {
			double h = low + (i * ((high - low) / slices));
			// For each height, create a list of lines from each polygon
			ArrayList<Line> lines = new ArrayList<Line>();
			for (Mesh.Polygon polygon : currentModel.polygons) {
				ArrayList<Line> edges = polygon.getLines();
				ArrayList<Vector3d> intersects = new ArrayList<Vector3d>();
				for (Line line : edges) {
					Vector3d intersect = line.xyIntersect(h);
					if (intersect != null)
						intersects.add(intersect);
				}
				if (intersects.size() == 2) {
					Line line = new Line(intersects.get(0), intersects.get(1));
					line.setFillColor(0, 0, 1);
					lines.add(line);
					geometry.add(line);
				}
			}
		}
		geometry.add(currentModel);
		geometry.add(boundingBox);

		// geometry.add(octreeBox);
	}


	public void sliceRadial(int slices) {
		if (currentModel == null)
			return;
		stackMostRecent = false;
		geometry.clear();
		// Loop through each slice
		for (int i = 0; i < slices; i++) {
			if (i > 80)
				continue;
			double r = UtilMath.TWO_PI / slices * i;
			// For each slice, create a list of lines from each polygon
			ArrayList<Line> lines = new ArrayList<Line>();
			for (Mesh.Polygon polygon : currentModel.polygons) {
				ArrayList<Line> edges = polygon.getLines();
				ArrayList<Vector3d> intersects = new ArrayList<Vector3d>();
				for (Line line : edges) {
					Vector3d intersect = line.radialIntersect(r);
					if (intersect == null)
						continue;
					if (Math.abs(intersect.x) > 0.1 && Math.abs(intersect.y) > 0.1) {
						if (checkQuadrant(intersect, r) == false)
							continue;
					}
					else {
						// System.out.println(intersect);
					}
					intersects.add(intersect);
				}
				if (intersects.size() == 2) {
					Line line = new Line(intersects.get(0), intersects.get(1));
					line.setFillColor(0, 0, 1);
					lines.add(line);
					geometry.add(line);
				}
			}
		}
		geometry.add(currentModel);
		geometry.add(boundingBox);
		// geometry.add(octreeBox);

	}


	public void sliceVoxel() {

	}


	public boolean checkQuadrant(Vector3d v, double r) {
		if (v == null)
			return (false);
		double r2 = (float) Math.atan2(v.y, v.x);
		double delta = Util.absoluteAngleDifference(r, r2);
		if (delta < UtilMath.HALF_PI)
			return (true);
		else {
			return (false);
		}
	}

	/*
	 * public Vector6 getMaxVoxel() { Vector6 out = new Vector6(); float f =
	 * minimumVoxelSize.get(); while(f < boundingBox.size.x || f <
	 * boundingBox.size.y || f < boundingBox.size.z) { f *= 2; } out.x = out.y =
	 * out.z = f; return(out); }
	 */


	public void scaleModelTo(double size) {
		double s = 1;

		double boundingBoxMax = boundingBox.getLongestEdge();

		s = size / boundingBoxMax;

		currentModel.scale(s);
	}


	@Override
	public void setupControl() {
		sliceRadialButton = new UIEButton(this, "sliceRadial", "Slice Radially", 0, 0, 20, 20);
		controllerManager.add(sliceRadialButton);

		sliceStackButton = new UIEButton(this, "sliceStack", "Slice Vertically", 0, 0, 20, 20);
		controllerManager.add(sliceStackButton);

		// sliceAmountField = new UIETextField(this,"sliceAmount","Number of
		// Slices",sliceAmount,20,getHeight() - 150,60,20);
		// controllerManager.add(sliceAmountField);

		String[] options = { "Visible", "Ghosted", "Invisible" };
		modelDropDown = new UIEDropdown(this, "modelDrop", "OBJ Visiblity", 0, 0, 80, 20, options);
		controllerManager.add(modelDropDown);

		boundingBoxCheckBox = new UIEToggle(this, "boundingToggle", "Show Bounding Box", 0, 0, 20, 20);
		controllerManager.add(boundingBoxCheckBox);

		// minimumVoxelField = new UIETextField(this,"minimumVoxelSize","Minimum Voxel
		// Size",minimumVoxelSize,20,80,60,20);
		// controllerManager.add(minimumVoxelField);

		controllerManager.finalize();
	}


	@Override
	public void message(EventMessage message) {
		UserInterfaceElement<? extends UserInterfaceElement<?>> controller = ((UIEEvent)message).element;
		
		String name = controller.getName();
		if (name.equals("sliceRadial")) {
			sliceRadial(currentSlices);
		}
		else if (name.equals("sliceStack")) {
			sliceStack(currentSlices);
		}
		else if (name.equals("sliceAmount")) {
			currentSlices = sliceAmount.get();
			if (stackMostRecent)
				sliceStack(currentSlices);
			else
				sliceRadial(currentSlices);

			minimumVoxelField.displayName = "Minum Voxel Size (Recomended: " + boundingBox.getFrame().m22() / sliceAmount.get() + ")";
		}
		else if (name.equals("modelDrop")) {
			if (modelDropDown.selectedValue == 0)
				currentModel.graphicSetting = Mesh.VISIBLE;
			else if (modelDropDown.selectedValue == 1)
				currentModel.graphicSetting = Mesh.GHOSTED;
			else if (modelDropDown.selectedValue == 2)
				currentModel.graphicSetting = Mesh.INVISIBLE;
		}
		else if (name.equals("boundingToggle")) {
			// System.out.println(geometry.get("#bounding_box"));
			geometry.get("#bounding_box").visible = boundingBoxCheckBox.state;
		}
		/*
		 * else if (name.equals("minimumVoxelSize")) { octreeBox = new
		 * Box(getMaxVoxel()); geometry.replace("#octree_box",octreeBox); }
		 */
		
	}
}
