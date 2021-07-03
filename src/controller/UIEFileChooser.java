package controller;

import java.io.File;

import javax.swing.JFileChooser;

import fonts.BitmapFont;


public class UIEFileChooser extends UserInterfaceElement implements Controllable {
	
	public String text = "";
	
	public UIEButton button;
	public UIETextField field;
	
	File file;
	
	UIEControlManager manager;
	
	public int mode = JFileChooser.FILES_ONLY;

	public UIEFileChooser(Controllable target,String name,String displayName, int x, int y, int width, int height,UIEControlManager manager,boolean selectFiles,boolean selectDirectories) {
		super(target,name,displayName,x,y,width,height);
		
		field = new UIETextField(target,"chooser_field",displayName, x,y, width - height - 10,height);
		button = new UIEButton(target,"chooser_button","",x + width - height, y , height,height);
		
		if (selectFiles && selectDirectories) {
			mode = JFileChooser.FILES_AND_DIRECTORIES;
		}
		else if (selectFiles) {
			mode = JFileChooser.FILES_ONLY;
		}
		else if (selectDirectories) {
			mode = JFileChooser.DIRECTORIES_ONLY;
		}
		
		this.manager = manager;
		
	}
	
	@Override
	public void render() {
		field.render();
		//BitmapFont.drawString(text, x + displayX, y + displayY,null);
		button.render();
	}
	
	@Override
	public void setPosition(int x, int y) {
		super.setPosition(x,y);
		
		field.x = this.x;
		field.y = this.y;
		
		button.x = this.x + width - height;
		button.y = this.y;
		
	}
	
	public boolean pick(int x, int y) {
		boolean pick = false;
		if (button.pick(x, y)) {
			pick = true;
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(mode);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				field.currentString = file.getAbsolutePath();
				text = field.currentString;
				execute();
			}
		}
		else if (field.pick(x, y)) {
			manager.setKeyboardTarget(field);
			pick = true;
		}
		return(pick);
	}

	@Override
	public void controllerEvent(UserInterfaceElement controller) {
		if (name.equals("chooser_field")) {
			file = new File(field.currentString);
		}
		
	}

	@Override
	public int getX() {
		return(x);
	}

	@Override
	public int getY() {
		return(y);
	}

	@Override
	public int getWidth() {
		return(width);
	}

	@Override
	public int getHeight() {
		return(height);
	}
	
	public void execute() {}

	@Override
	public void keyPressed(int key) {
	}
	
	@Override
	public void textInput(char character) {
	}

}
