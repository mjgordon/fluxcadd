package ui;

import controller.*;
import main.FluxCadd;

public class Content_Chooser extends Content implements Controllable {

	private UIEControlManager controllerManager;
	private UIEButton buttonCAD;
	private UIEButton buttonCAM;

	public Content_Chooser(Panel parent) {
		super(parent);

		controllerManager = new UIEControlManager(getWidth(),getHeight(),10,50,10,10);

		buttonCAD = new UIEButton(this, "button_cad", "CAD Module", 10, 10, 100, 100);
		controllerManager.add(buttonCAD);

		buttonCAM = new UIEButton(this, "button_cam", "CAM Module", 120, 10, 100, 100);
		controllerManager.add(buttonCAM);
		
	}

	@Override
	public void controllerEvent(UserInterfaceElement controller) {
		String name = controller.getName();
		if (name.equals("button_cad")) {
			FluxCadd.panelManager.resetPanels();
			FluxCadd.panelManager.initCADWindows();
		}
		else if (name.equals("button_cam")) {
			FluxCadd.panelManager.resetPanels();
			FluxCadd.panelManager.initCAMWindows();
		}

	}

	@Override
	public void render() {
		controllerManager.render();
	}

	@Override
	protected void keyPressed(int key) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void textInput(char character) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void mouseWheel(float amt) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
		if (button == 0) {
			controllerManager.poll(mouseX,mouseY);
		}

	}

	@Override
	protected void mouseDragged(int dx, int dy) {
		// TODO Auto-generated method stub

	}

}
