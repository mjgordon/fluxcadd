package controller;

import java.util.ArrayList;
import java.util.Arrays;

import fonts.BitmapFont;
import graphics.Primitives;
import utility.Util;

public class UIEDropdown extends UserInterfaceElement {

	private ArrayList<String> values;
	public int selectedValue;

	private boolean open = false;

	public UIEDropdown(Controllable target, String name, String displayName, int x, int y, int width, int height, String[] values) {
		super(target, name,displayName, x, y, width, height);
		this.values = new ArrayList<String>(Arrays.asList(values));
	}

	public boolean pick(int mouseX, int mouseY) {
		boolean pick = false;
		
		if (open) {

			if (mouseX > this.x && mouseX < this.x + width && mouseY > this.y && mouseY < this.y + (height * (values.size() + 1))) {
				int id = (int)(1.0 * (mouseY - y - height) / height);
				if (id < 0) return(false);
				selectedValue = id;
				open = !open;
				pick = true;
				execute();
			}
		}

		if (super.pick(mouseX, mouseY)) {
			open = !open;
			pick = true;
		}

		return (pick);
	}

	public void render() {
		Util.fill(255,255,255);
		if (selected) {
			Util.stroke(0, 0, 255);
		}	
		else {
			Util.stroke(0, 0, 0);
		}
		Primitives.rect(x, y, width, height);

		if (open) {
			Util.fill(220,220,220);
			for (int i = 0; i < values.size(); i++) {
				int yPos = y + (height * (i + 1));
				Primitives.rect(x, yPos, width, height);
				BitmapFont.drawString(values.get(i), x + 3, yPos + 5,null);
			}

		}
		
		Util.color(0, 0, 0);
		BitmapFont.drawString(values.get(selectedValue), x + 3, y + 5,null);
		BitmapFont.drawString(displayName, x + displayX, y + displayY,null);
	}


	public int getValueId() {
		return (selectedValue);
	}

	public String getValueName() {
		return (values.get(selectedValue));
	}
	
	public void setValueId(int id) {
		selectedValue = id;
	}

	@Override
	public void keyPressed(int key) {
	}

	@Override
	public void textInput(char character) {
	}
}