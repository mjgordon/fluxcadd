package robocam;

import controller.*;
import ui.Content_View;
import ui.Window;
import ui.WindowContent;

public class Content_Cam extends WindowContent implements Controllable {

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
	
	public Content_Cam(Window parent,Content_View previewWindow) {
		this.parent = parent;
		
		this.previewWindow = previewWindow;
		
		controllerManager = new ControllerManager(this);
		controllerManager.setParent(this);
		String[] moduleNames = {"Stacking","Drawing","Stamping"};
		drop = new Controller_DropDown(controllerManager,"drop_module","Module",20,parent.getHeight() - 60,100,20,moduleNames);
		controllerManager.add(drop);
		
		controllerManager.add(new Controller_FileChooser(controllerManager,"chooser_input",10,10,parent.getWidth()-20,20));
		
		parent.windowTitle = "RoboCam";
		
		module = new Module_Stacker(this,previewWindow);
	}
	
	public void keyPressed() {
		controllerManager.keyPressed();
		module.keyPressed();
		
	}

	public void render() {
		module.render();
		
		
		controllerManager.render();
		
		
		
	}

	public void mouseWheel(float amt) {
		// TODO Auto-generated method stub	
	}

	public void mousePressed() {
		if (!controllerManager.poll()) module.poll();
	}

	public void mouseDragged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerEvent(String name) {
		System.out.println("WindowContent Event: " + name);
		if (name.equals("drop_module")) {
			System.out.println("THIS DOESN'T DO ANYTHING RIGHT NOW");
		}
		
	}

}
