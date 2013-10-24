package controller;

import java.io.File;
import javax.swing.JFileChooser;
import fonts.PointFont;

public class Controller_FileChooser extends Controller implements Controllable {
	
	public String text = "";
	
	public Controller_Button button;
	public Controller_TextField field;
	
	File file;

	public Controller_FileChooser(ControllerManager parent,String name, int x, int y, int width, int height) {
		super(parent,name,x,y,width,height);
		
		field = new Controller_TextField(parent,"chooser_field","File: ", x,y, width - height - 10,height);
		button = new Controller_Button(parent,"chooser_button","",x + width - height, y , height,height);
		
	}
	
	@Override
	public void render() {
		field.render();
		PointFont.drawString(text, x + 3, y + 5);
		button.render();
	}
	
	public boolean pick(int x, int y) {
		boolean pick = false;
		if (button.pick(x, y)) {
			pick = true;
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showOpenDialog(chooser);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				field.currentString = file.getAbsolutePath();
				
			}
		}
		else if (field.pick(x, y)) {
			parent.setKeyboardTarget(field);
			pick = true;
		}
		return(pick);
	}

	@Override
	public void controllerEvent(String name) {
		if (name.equals("chooser_field")) {
			file = new File(field.currentString);
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
	
	public void execute() {}

}
