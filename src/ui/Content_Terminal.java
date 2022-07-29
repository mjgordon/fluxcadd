package ui;


import console.Console;
import console.ConsoleEvent;
import controller.Controllable;
import controller.UserInterfaceElement;
import controller.UIEControlManager;
import controller.UIETerminal;
import event.EventListener;
import event.EventMessage;


/**
 * 
 * @author Matt Gordon
 *
 */

public class Content_Terminal extends Content implements EventListener, Controllable {
	
	private UIEControlManager controllerManager;
	private UIETerminal terminal;

	
	public Content_Terminal(Panel parent) {
		super(parent);
		Console.instance().register(this);
		
		controllerManager = new UIEControlManager(getWidth() ,getHeight(),0,0,0,0);
		controllerManager.setCurrentY(0);
		terminal = new UIETerminal(this,"terminal","Terminal",0,0,getWidth(),60);
		controllerManager.add(terminal);
		
		controllerManager.finalize();
	}
	

	@Override
	public void message(EventMessage message) {
		if (message instanceof ConsoleEvent) {
			ConsoleEvent event = (ConsoleEvent) message;
			terminal.addString(event.data);
		}
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
	protected void mouseWheel(float amt) {
		terminal.mouseWheel(amt);
		
	}

	@Override
	protected void mousePressed(int button, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void mouseDragged(int dx, int dy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerEvent(UserInterfaceElement controller) {
		// TODO Auto-generated method stub
		
	}
}
