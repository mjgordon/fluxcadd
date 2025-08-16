package ui;

import controller.*;
import main.FluxCadd;

public class Content_Chooser extends Content {

	private UIEControlManager controllerManager;


	public Content_Chooser(Panel parent) {
		super(parent);
		setupControl();
	}


	@Override
	public void render() {
		controllerManager.render();
	}


	@Override
	protected void keyPressed(int key) {
	}


	@Override
	protected void textInput(char character) {
	}


	@Override
	protected void mouseWheel(int mouseX, int mouseY, int wheelDY) {
	}


	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
		if (button == 0) {
			controllerManager.mousePressed(mouseX, mouseY);
		}
	}


	@Override
	protected void mouseDragged(int button, int x, int y, int dx, int dy) {
	}


	@Override
	public void resizeRespond(int newWidth, int newHeight) {
		controllerManager.reflow();
	}


	private void setupControl() {
		controllerManager = new UIEControlManager(0, 0, getWidth(), getHeight(), 10, 50, 10, 10, false);

		controllerManager.add(new UIEButton("button_cad", "CAD Module", 10, 10, 100, 100).setCallback((b) -> {
			FluxCadd.panelManager.initCADWindows();
		}));

		controllerManager.add(new UIEButton("button_sdf", "SDF Module", 230, 10, 100, 100).setCallback((b) -> {
			FluxCadd.panelManager.initSDFWindows();
		}));
	}


	@Override
	protected void mouseReleased(int button) {
		// TODO Auto-generated method stub
	}
}
