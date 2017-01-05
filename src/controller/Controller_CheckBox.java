package controller;

import utility.Util;
import fonts.PointFont;

public class Controller_CheckBox extends Controller {

	public boolean state = true;
	
	public Controller_CheckBox(ControllerManager parent, String name,String displayName, int x, int y, int width, int height) {
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
		
		if (state) {
			Util.line(x, y, x + width, y + height);
			Util.line(x + width, y , x , y + height);
		}
		
		PointFont.drawString(displayName, x, y + 22);
	}

	@Override
	public void execute() {
		state = !state;
		parent.controllerEvent(name);
	}

	@Override
	public void keyPressed(int key) {
		// TODO Auto-generated method stub
		
	}

}
