package controller;

import java.io.File;

import javax.swing.JFileChooser;

public class UIEFileChooser extends UserInterfaceElement<UIEFileChooser> implements Controllable {

	public UIEButton button;
	public UIETextField field;

	// File file;

	UIEControlManager manager;

	public int mode = JFileChooser.FILES_ONLY;


	public UIEFileChooser(Controllable target, String name, String displayName, int x, int y, int width, int height, UIEControlManager manager, boolean selectFiles,
			boolean selectDirectories) {
		super(target, name, displayName, x, y, width, height);

		field = new UIETextField(this, "chooser_field", displayName, x, y, width - height - 10, height);
		button = new UIEButton(this, "chooser_button", "", x + width - height, y, height, height);

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

		debugOutlineColor = 0x00FF00;
	}


	public void setValue(String s) {
		field.setValue(s);
		execute();
	}


	@Override
	public void render() {
		field.render();
		// BitmapFont.drawString(text, x + displayX, y + displayY,null);
		button.render();

		super.render();
	}


	@Override
	public void setPosition(int x, int y) {
		super.setPosition(x, y);

		field.x = this.x;
		field.y = this.y;

		button.x = this.x + width - height;
		button.y = this.y;
	}


	public String getCurrentString() {
		return (field.getValue());
	}


	@Override
	public UIEFileChooser pick(int x, int y) {
		boolean pick = false;
		if (button.pick(x, y) == button) {
			pick = true;
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(mode);
			int returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				field.currentString = file.getAbsolutePath();
				execute();
			}
		}
		else if (field.pick(x, y) == field) {
			manager.setKeyboardTarget(field);
			pick = true;
		}

		if (pick) {
			return (this);
		}
		else {
			return (null);
		}
	}


	@Override
	public void controllerEvent(UserInterfaceElement<? extends UserInterfaceElement<?>> controller) {
		execute();
	}


	public void setWidth(int width) {
		super.setWidth(width);
		field.setWidth(width);
	}



	@Override
	public void keyPressed(int key) {
	}


	@Override
	public void textInput(char character) {
	}
}
