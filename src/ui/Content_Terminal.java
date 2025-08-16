package ui;

import console.Console;
import controller.UIEControlManager;
import controller.UIETerminal;
import event.EventListener;
import event.EventMessage;

/**
 * Panel content for the terminal, contains only the single UIETerminal object
 * @author Matt Gordon
 *
 */

public class Content_Terminal extends Content implements EventListener {

	private UIEControlManager controllerManager;
	private UIETerminal terminal;


	public Content_Terminal(Panel parent) {
		super(parent);
		Console.instance().register(this);

		controllerManager = new UIEControlManager(0, 0, getWidth(), getHeight(), 0, 0, 0, 0, false);
		controllerManager.setCurrentY(0);
		terminal = new UIETerminal("terminal", "Terminal", 0, 0, getWidth(), 60);
		controllerManager.add(terminal);

		controllerManager.finalizeLayer();
	}


	@Override
	public void message(EventMessage message) {
		terminal.addString(message.data);
	}


	@Override
	public void render() {
		controllerManager.render();
	}


	@Override
	protected void keyPressed(int key) {
		terminal.keyPressed(key);
	}


	@Override
	protected void textInput(char character) {
		terminal.textInput(character);
	}


	@Override
	protected void mouseWheel(int mouseX, int mouseY, int wheelDY) {
		terminal.mouseWheel(wheelDY);
	}


	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
	}


	@Override
	protected void mouseDragged(int button, int x, int y, int dx, int dy) {
	}


	@Override
	public void resizeRespond(int newWidth, int newHeight) {
		controllerManager.reflow();
		controllerManager.finalizeLayer();
	}


	@Override
	protected void mouseReleased(int button) {
		// TODO Auto-generated method stub
		
	}
}
