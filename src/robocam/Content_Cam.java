package robocam;

import controller.*;
import ui.Content_View;
import ui.Panel;
import ui.Content;

public class Content_Cam extends Content implements Controllable {

	/**
	 * Contains all the controllers currently used by the module. Recreated on every module switch. 
	 */
	private ControllerManager controllerManager;
	
	/**
	 * The only constant controller, the switcher between different modules. 
	 */
	private Controller_DropDown drop;
	
	private Module module;
	
	private Content_View previewWindow;
	
	public Content_Cam(Panel parent,Content_View previewWindow) {
		super(parent);
		
		this.previewWindow = previewWindow;
		
		controllerManager = new ControllerManager(this);
		controllerManager.setParent(this);
		String[] moduleNames = {"Stacking","Plotting","Router","Drawbot"};
		drop = new Controller_DropDown(controllerManager,"drop_module","Module",20,parent.getHeight() - 60,100,20,moduleNames);
		controllerManager.add(drop);
		drop.selectedValue = 2;
		
		
		parent.windowTitle = "Robo";
		
		//Defaults to Plotter Mode
		module = new Module_Drawbot(this,previewWindow);
	}
	
	public void render() {
		module.render();	
		controllerManager.render();
	}
	
	@Override
	protected void keyPressed(int key) {
		controllerManager.keyPressed(key);
		module.keyPressed(key);
		
	}
	
	@Override 
	protected void textInput(char character) {
		controllerManager.textInput(character);
		module.textInput(character);
	}

	@Override
	protected void mousePressed(int button,int mouseX, int mouseY) {
		if (button == 0) {
			if (!controllerManager.poll(mouseX,mouseY)) module.poll(mouseX, mouseY);
		}	
	}

	@Override
	protected void mouseDragged(int dx, int dy) {}
	
	@Override
	protected void mouseWheel(float amt) {}

	@Override
	public void controllerEvent(String name) {
		System.out.println("WindowContent Event: " + name);
		if (name.equals("drop_module")) {
			String value = drop.getValueName();
			if (value.equals("Stacking")) module = new Module_Stacker(this,previewWindow);
			else if (value.equals("Plotting")) module = new Module_Plotter(this,previewWindow);
			else if (value.equals("Router")) module = new Module_Router(this,previewWindow);
			else if (value.equals("Drawbot")) module = new Module_Drawbot(this,previewWindow);
		}
		
	}

}
