package robocam;

import controller.*;
import ui.Content_View;
import ui.Panel;
import ui.Content;

public class Content_Cam extends Content implements Controllable {

	/**
	 * Contains all the controllers currently used by the module. Recreated on every module switch. 
	 */
	ControllerManager controllerManager;
	
	/**
	 * The only constant controller, the switcher between different modules. 
	 */
	Controller_DropDown drop;
	
	Module module;
	
	Content_View previewWindow;
	
	public Content_Cam(Panel parent,Content_View previewWindow) {
		this.parent = parent;
		
		this.previewWindow = previewWindow;
		
		controllerManager = new ControllerManager(this);
		controllerManager.setParent(this);
		String[] moduleNames = {"Stacking","Plotting","Router"};
		drop = new Controller_DropDown(controllerManager,"drop_module","Module",20,parent.getHeight() - 60,100,20,moduleNames);
		controllerManager.add(drop);
		drop.selectedValue = 2;
		
		
		parent.windowTitle = "RoboCam";
		
		//Defaults to Plotter Mode
		module = new Module_Router(this,previewWindow);
	}
	
	
	public void keyPressed(int key) {
		controllerManager.keyPressed(key);
		module.keyPressed(key);
		
	}

	public void render() {
		module.render();	
		controllerManager.render();
	}

	public void mouseWheel(float amt) {
		// TODO Auto-generated method stub	
	}

	public void mousePressed(int button,int mouseX, int mouseY) {
		if (!controllerManager.poll(mouseX,mouseY)) module.poll(mouseX, mouseY);
	}

	public void mouseDragged(int dx, int dy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerEvent(String name) {
		System.out.println("WindowContent Event: " + name);
		if (name.equals("drop_module")) {
			String value = drop.getValueName();
			if (value.equals("Stacking")) module = new Module_Stacker(this,previewWindow);
			else if (value.equals("Plotting")) module = new Module_Plotter(this,previewWindow);
			else if (value.equals("Router")) module = new Module_Router(this,previewWindow);
		}
		
	}

}
