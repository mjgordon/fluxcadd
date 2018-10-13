package robocam;

import geometry.Line;
import geometry.Point;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import ui.Content_View;
import ui.ViewType;
import ui.Content;
import utility.MutableFloat;
import utility.PVector;
import utility.Vector6;
import console.Console;
import controller.Controllable;
import controller.ControllerManager;
import controller.Controller_Button;
import controller.Controller_DropDown;
import controller.Controller_TextField;

public class Module_Stacker extends Module implements Controllable {
	
	public MutableFloat cupOriginX = new MutableFloat(0);
	public MutableFloat cupOriginY = new MutableFloat(0);
	public MutableFloat cupOriginZ = new MutableFloat(0);
	public MutableFloat stackOriginX = new MutableFloat(100);
	public MutableFloat stackOriginY = new MutableFloat(-140);
	public MutableFloat stackOriginZ = new MutableFloat(0);
	public MutableFloat cupDiameterSmall = new MutableFloat(53);
	public MutableFloat cupDiameterLarge = new MutableFloat(78);
	public MutableFloat cupHeight = new MutableFloat(102);
	public MutableFloat cupOffset = new MutableFloat(7);
	public MutableFloat stackSize = new MutableFloat(5);
	
	Controller_DropDown modeDrop;
	
	public static final int GRIP_MODE_OUTER = 0;
	public static final int GRIP_MODE_INNER = 1;
	
	public static int gripMode = GRIP_MODE_OUTER;
	
	public static ArrayList<String> output;
	public float feedHeight;
	
	public Module_Stacker(Content parent,Content_View associatedView) {
		super(parent,associatedView);
		
		associatedView.changeType(ViewType.PERSP);
		
		setupControl();
	}
	
	public void controllerEvent(String name) {
		System.out.println(name);
		if (name.equals("export")) {
			
			output = new ArrayList<String>();
			
			try {
				BufferedReader prefixReader = new BufferedReader(new FileReader("scripts/prefix.txt"));
				String line = prefixReader.readLine();
				while(line != null) {
					output.add(line);
					line = prefixReader.readLine();
				}
				prefixReader.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			output.add("\n; === Begin generated code ===\n");
			
			output.add("$BASE = BASE_DATA[2]");
			output.add("$TOOL = TOOL_DATA[2]");
			output.add("PTP {X 0, Y 0, Z 300,B 180}");
			output.add("rSP = 'HFF'");
			output.add("rPR = 'H00'");
			output.add("WAIT SEC 1");
			
			if (modeDrop.getValueName().equals("2d")) make2d(true);
			else make3d(true);
			
			output.add("\n; === End generated code ===\n");
			
			try {
				BufferedReader suffixReader = new BufferedReader(new FileReader("scripts/suffix.txt"));
				String line = suffixReader.readLine();
				while(line != null) {
					output.add(line);
					line = suffixReader.readLine();
				}
				suffixReader.close();
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			PrintWriter out;
			try {
				out = new PrintWriter("gen_stack.src");
				for (String s : output) out.println(s);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}		
		}
		//Update from any other component
		else {
			activate();
			if (modeDrop.getValueName().equals("2d")) make2d(false);
			else make3d(false);
			
			for (int i = 0; i< toolPath.size()-1; i++) {
				Vector6 v = toolPath.get(i);
				Vector6 v2 = toolPath.get(i+1);
				Line l = new Line(new PVector(v.x,v.y,v.z), new PVector(v2.x,v2.y,v2.z));
				float g = (i * 1.f) / (toolPath.size()-1);
				float b = 1 - g;
				l.setColor(0,g,b);
				geometry.add(l);
			}
			
			for (int i = 0; i < endPoints.size(); i++) {
				Point point = new Point(endPoints.get(i));
				float g = (i * 1.f) / (endPoints.size()-1);
				float b = 1 - g;
				point.setColor(0,g,b);
				geometry.add(point);
			}
		}
	}
	
	public void make2d(boolean export) {
		
		int stackSize = (int)(this.stackSize.get() + 0);
		
		int cupTotal = 0;
		for (int i = 1; i <= stackSize; i++) cupTotal += i;
		feedHeight = (cupTotal-1) * cupOffset.get();
		if (export) {
			Console.instance().log("Exporting .src File");
			Console.instance().log("Cup Total: " + cupTotal);
			Console.instance().log("Feed Height: " + feedHeight);
		}	
		//For each layer in the stack
		for (int i = stackSize; i > 0; i--) {
			int layerHeight = stackSize - i;
			//For each cup in the layer
			for (int k = 0; k < i ; k++) {
				float endX = stackOriginX.get() + (k * cupDiameterLarge.get()) + (layerHeight * cupDiameterLarge.get() / 2);
				float endY = stackOriginY.get();
				float endZ = stackOriginZ.get() + (layerHeight * cupHeight.get());
				
				pickUp(export);
			  
				dropOff(export,endX,endY,endZ);
			}
		} 
	}
	
	public void make3d(boolean export) {
		int stackSize = (int)(this.stackSize.get() + 0);
		
		int cupTotal = 0;
		for (int i = 1; i<= stackSize; i++) cupTotal += (i*i);
		feedHeight = (cupTotal-1) * cupOffset.get();
		
		if (export) {
			Console.instance().log("Exporting .src File");
			Console.instance().log("Cup Total: " + cupTotal);
			Console.instance().log("Feed Height: " + feedHeight);
		}
		
		//For each layer in the stack
		for(int i = stackSize; i> 0 ; i--) {
			int layerHeight = stackSize - i;
			//For each cup in the layer
			for (int k = 0; k < i * i; k ++) {
				int x = k % i;
				int y = k / i;

				float endX = stackOriginX.get() + (x * cupDiameterLarge.get()) + (layerHeight * cupDiameterLarge.get() / 2);
				float endY = stackOriginY.get() + (y * cupDiameterLarge.get()) + (layerHeight * cupDiameterLarge.get() / 2);
				float endZ = stackOriginZ.get() + (layerHeight * cupHeight.get());
				
				pickUp(export);
				
				dropOff(export,endX,endY,endZ);
			}
		}
	}

	public void pickUp(boolean export) {
		if (export) {
			output.add("LIN { X " + cupOriginX.get() + ",Y " + cupOriginY.get() + ",Z "
					+ (cupOriginZ.get() + feedHeight + (cupHeight.get() * 2)) + "}");
			output.add("LIN { Z " + (cupOriginZ.get() + feedHeight) + "}");
			if (gripMode == GRIP_MODE_OUTER)
				output.add("rPR = 'H66' ; Gripper close");
			else
				output.add("rPR ='H00' ; Gripper open");
			output.add("WAIT SEC 1");
			output.add("LIN { Z " + (cupOriginZ.get() + feedHeight + (cupHeight.get() * 2))
					+ "}\n");

		}
		
		else {
			toolPath.add(new Vector6(cupOriginX.get(),cupOriginY.get(),cupOriginZ.get() + feedHeight + (cupHeight.get() * 2)));
			toolPath.add(new Vector6(cupOriginX.get(),cupOriginY.get(),cupOriginZ.get() + feedHeight));
			//toolPath.add(((new Vector6()).setZ(cupOriginZ + feedHeight)) );
			toolPath.add(new Vector6(cupOriginX.get(),cupOriginY.get(),cupOriginZ.get() + feedHeight + (cupHeight.get() * 2)));
			//toolPath.add(((new Vector6()).setZ(cupOriginZ + feedHeight + (cupHeight * 2))) );	
		}

		feedHeight -= cupOffset.get();
	}

	public void dropOff(boolean export, float x, float y, float z) {
		if (export) {
			output.add("LIN { X " + x + ",Y " + y + ",Z "
					+ (z + (cupHeight.get() * 2)) + "}");
			output.add("LIN { Z " + (z) + "}");
			if (gripMode == GRIP_MODE_OUTER)
				output.add("rPR = 'H00' ; Gripper open");
			else
				output.add("rPR ='H66' ; Gripper close");
			output.add("WAIT SEC 1");
			output.add("LIN { Z " + (z + (cupHeight.get() * 2)) + "}\n");
		}
		else {
			toolPath.add(new Vector6(x,y,z + (cupHeight.get() * 2)));
			toolPath.add(new Vector6(x,y,z));
			//toolPath.add( ((new Vector6()).setZ(z)) );
			toolPath.add(new Vector6(x,y,z + (cupHeight.get() * 2)));
			//toolPath.add( ((new Vector6()).setZ(z + (cupHeight * 2))));
			endPoints.add(new PVector(x,y,z));
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
	
	public void setupControl() {
		controllerManager = new ControllerManager(this);
		
		
		
		controllerManager.add(new Controller_TextField(controllerManager,"cupOriginX","Cup Origin X",cupOriginX,
				20,getHeight() - 120,60,20));
		controllerManager.add(new Controller_TextField(controllerManager,"cupOriginY","Y",cupOriginY,
				130,getHeight() - 120,60,20));
		controllerManager.add(new Controller_TextField(controllerManager,"cupOriginZ","Z",cupOriginZ,
				240,getHeight() - 120,60,20));
		
		controllerManager.add(new Controller_TextField(controllerManager,"stackOriginX","Stack Ori X",stackOriginX,
				20,getHeight() - 160,60,20));
		controllerManager.add(new Controller_TextField(controllerManager,"stackOriginY","Y",stackOriginY,
				130,getHeight() - 160,60,20));
		controllerManager.add(new Controller_TextField(controllerManager,"stackOriginZ","Z",stackOriginZ,
				240,getHeight() - 160,60,20));
		
		controllerManager.add(new Controller_TextField(controllerManager,"cupDiameterSmall","Cup Small Diameter",cupDiameterSmall,
				20,getHeight() - 220,60,20));
		controllerManager.add(new Controller_TextField(controllerManager,"cupDiameterLarge","Cup Large Diameter",cupDiameterLarge,
				170,getHeight() - 220,60,20));
		controllerManager.add(new Controller_TextField(controllerManager,"cupHeight","Cup Height",cupHeight,
				20,getHeight() - 260,60,20));
		controllerManager.add(new Controller_TextField(controllerManager,"cupOffset","Cup Offset",cupOffset,
				170,getHeight() - 260,60,20));
		
		controllerManager.add(new Controller_TextField(controllerManager,"stackHeight","Stack Height",stackSize,
				20,getHeight() - 320,60,20));
		
		controllerManager.add(new Controller_Button(controllerManager,"export","Export",
				20,getHeight() - 380,20,20));
		
		String[] modeNames = {"2d","3d"};
		modeDrop =  new Controller_DropDown(controllerManager, "modeDrop", "Stack Mode", 170, getHeight() - 320, 60, 20, modeNames);
		controllerManager.add(modeDrop);
	}

}