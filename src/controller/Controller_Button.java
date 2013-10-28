package controller;

import fonts.PointFont;
import utility.Util;

public class Controller_Button extends Controller {
	public Controller_Button(ControllerManager parent,String name,String displayName, int x, int y, int width, int height) {
		super(parent, name,x,y,width,height);
		this.displayName = displayName;
	}
	
	public boolean pick (int x, int y) {
		if (super.pick(x,y)) {
			execute();
			return(true);
		}
		return(false);
	}

	@Override
	public void render() {
		Util.fill(255,255,255);
		Util.stroke(0,0,0);
		
		Util.rect(x, y, width, height);
		
		Util.noFill();
		
		Util.rect(x + 5, y + 5, width - 10, height - 10);
		
		PointFont.drawString(displayName, x, y + 22);
	}
	
	public void execute() {
		parent.controllerEvent(name);
	}
}
