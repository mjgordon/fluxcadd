package controller;

import java.util.ArrayList;
import java.util.Arrays;

import fonts.PointFont;
import utility.Util;

public class Controller_DropDown extends Controller {

	private ArrayList<String> values;
	public int selectedValue;

	private boolean open = false;

	public Controller_DropDown(ControllerManager parent, String name, String displayName, int x, int y, int width, int height, String[] values) {
		super(parent, name, x, y, width, height);
		this.displayName = displayName;
		this.values = new ArrayList<String>(Arrays.asList(values));
	}

	public boolean pick(int x, int y) {
		boolean pick = false;
		if (open) {
			if (x > this.x && x < this.x + width && y < this.y && y > this.y - (height * values.size())) {
				int id = (this.y - y) / height;
				selectedValue = id;
				open = !open;
				pick = true;
				execute();
			}
		}

		if (super.pick(x, y)) {
			open = !open;
			pick = true;
		}

		return (pick);
	}

	public void render() {
		Util.fill(1, 1, 1);
		if (selected)
			Util.stroke(0, 0, 255);
		else
			Util.stroke(0, 0, 0);
		Util.rect(x, y, width, height);

		if (open) {
			Util.fill(220,220,220);
			for (int i = 0; i < values.size(); i++) {
				int yPos = y - (height * (i + 1));
				Util.rect(x, y - (height * (i + 1)), width, height);
				PointFont.drawString(values.get(i), x + 3, yPos + 5);
			}

		}
		Util.color(0, 0, 0);
		PointFont.drawString(values.get(selectedValue), x + 3, y + 5);
		PointFont.drawString(displayName, x, y + 22);
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