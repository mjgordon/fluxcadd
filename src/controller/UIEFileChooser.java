package controller;

import java.io.File;

import javax.swing.JFileChooser;

public class UIEFileChooser extends UserInterfaceElement<UIEFileChooser> {

	private UIEButton button;
	private UIETextField field;

	private UIEControlManager manager;

	private int mode = JFileChooser.FILES_ONLY;


	public UIEFileChooser(String name, String displayName, int x, int y, int width, int height, UIEControlManager manager, boolean selectFiles, boolean selectDirectories) {
		super(name, displayName, x, y, width, height);

		field = new UIETextField("chooser_field", displayName, x, y, width - height - 10, height).setCallback((field) -> {
			execute();
		});
		button = new UIEButton("chooser_button", "", x + width - height, y, height, height).setCallback((button) -> {
			execute();
		});

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
				field.setValueSilent(file.getAbsolutePath());
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


	public void setWidth(int width) {
		super.setWidth(width);
		field.setWidth(width);
	}
}
