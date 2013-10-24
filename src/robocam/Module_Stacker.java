package robocam;

import geometry.Point;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import main.Param0;

import ui.Content_View;
import ui.WindowContent;
import controller.Controllable;
import controller.ControllerManager;
import controller.Controller_Button;
import controller.Controller_DropDown;
import controller.Controller_TextField;
import data.GeometryFile;

public class Module_Stacker extends Module implements Controllable {
	
	public Float cupOriginX = new Float(0);
	public Float cupOriginY = new Float(0);
	public Float cupOriginZ = new Float(0);
	public Float stackOriginX = new Float(100);
	public Float stackOriginY = new Float(-140);
	public Float stackOriginZ = new Float(0);
	public Float cupDiameterSmall = new Float(53);
	public Float cupDiameterLarge = new Float(78);
	public Float cupHeight = new Float(102);
	public Float cupOffset = new Float(7);
	public Float stackSize = new Float(5);
	
	Controller_DropDown modeDrop;
	
	public static final int GRIP_MODE_OUTER = 0;
	public static final int GRIP_MODE_INNER = 1;
	
	public static int gripMode = GRIP_MODE_OUTER;
	
	public static ArrayList<String> output;
	public float feedHeight;
	
	Content_View associatedView;
	
	ArrayList<Vector6> toolPath;
	
	public Module_Stacker(WindowContent parent,Content_View associatedView) {
		this.parent = parent;
		
		geometry = new GeometryFile();
		geometry.add(new Point(0,0,0));
		
		toolPath = new ArrayList<Vector6>();
		
		this.associatedView = associatedView;
		activate();
		
		setupControl();
	}
	
	public void activate() {
		associatedView.geometry = geometry;
	}

	@Override
	public void controllerEvent(String name) {
		if (name.equals("export")) {
			System.out.println("Exporting");
			
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
	}
	
	public void make2d(boolean export) {
		
		int stackSize = (int)(this.stackSize + 0);
		
		int cupTotal = 0;
		for (int i = 1; i <= stackSize; i++) cupTotal += i;
		Param0.printToTerminal("Exporting .src File");
		Param0.printToTerminal("Cup Total: " + cupTotal);
		feedHeight = (cupTotal-1) * cupOffset;
		Param0.printToTerminal("Feed Height: " + feedHeight);
		//For each layer in the stack
		for (int i = stackSize; i > 0; i--) {
			int layerHeight = stackSize - i;
			//For each cup in the layer
			for (int k = 0; k < i ; k++) {
				float endX = stackOriginX + (k * cupDiameterLarge) + (layerHeight * cupDiameterLarge / 2);
				float endY = stackOriginY;
				float endZ = stackOriginZ + (layerHeight * cupHeight);
				
				pickUp(true);
			  
				dropOff(true,endX,endY,endZ);
			}
		} 
	}
	
	public void make3d(boolean export) {
		int stackSize = (int)(this.stackSize + 0);
		
		int cupTotal = 0;
		for (int i = 1; i<= stackSize; i++) cupTotal += (i*i);
		feedHeight = (cupTotal-1) * cupOffset;
		//For each layer in the stack
		for(int i = stackSize; i> 0 ; i--) {
			int layerHeight = stackSize - i;
			//For each cup in the layer
			for (int k = 0; k < i * i; k ++) {
				int x = k % i;
				int y = k / i;

				float endX = stackOriginX + (x * cupDiameterLarge) + (layerHeight * cupDiameterLarge / 2);
				float endY = stackOriginY + (y * cupDiameterLarge) + (layerHeight * cupDiameterLarge / 2);
				float endZ = stackOriginZ + (layerHeight * cupHeight);
				
				pickUp(true);
				
				dropOff(true,endX,endY,endZ);
			}
		}
	}

	public void pickUp(boolean export) {
		if (export) {
			output.add("LIN { X " + cupOriginX + ",Y " + cupOriginY + ",Z "
					+ (cupOriginZ + feedHeight + (cupHeight * 2)) + "}");
			output.add("LIN { Z " + (cupOriginZ + feedHeight) + "}");
			if (gripMode == GRIP_MODE_OUTER)
				output.add("rPR = 'H66' ; Gripper close");
			else
				output.add("rPR ='H00' ; Gripper open");
			output.add("WAIT SEC 1");
			output.add("LIN { Z " + (cupOriginZ + feedHeight + (cupHeight * 2))
					+ "}\n");

		}
		
		else {
			toolPath.add(new Vector6(cupOriginX,cupOriginY,cupOriginZ + feedHeight + (cupHeight*2)));
			toolPath.add( ((new Vector6()).setZ(cupOriginZ + feedHeight)) );
			toolPath.add( ((new Vector6()).setZ(cupOriginZ + feedHeight + (cupHeight * 2))) );
		}

		feedHeight -= cupOffset;
	}

	public void dropOff(boolean export, float x, float y, float z) {
		if (export) {
			output.add("LIN { X " + x + ",Y " + y + ",Z "
					+ (z + (cupHeight * 2)) + "}");
			output.add("LIN { Z " + (z) + "}");
			if (gripMode == GRIP_MODE_OUTER)
				output.add("rPR = 'H00' ; Gripper open");
			else
				output.add("rPR ='H66' ; Gripper close");
			output.add("WAIT SEC 1");
			output.add("LIN { Z " + (z + (cupHeight * 2)) + "}\n");
		}
		else {
			toolPath.add(new Vector6(x,y,z + (cupHeight * 2)));
			toolPath.add( ((new Vector6()).setZ(z)) );
			toolPath.add( ((new Vector6()).setZ(z + (cupHeight * 2))));
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
