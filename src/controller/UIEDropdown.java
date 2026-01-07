package controller;

import java.util.ArrayList;
import java.util.Arrays;

import graphics.Graphics2D;
import utility.Color3i;

public class UIEDropdown extends UserInterfaceElement<UIEDropdown> {

	private ArrayList<String> values;
	public int selectedValue;

	private boolean open = false;


	public UIEDropdown(String name, String displayName, int x, int y, int width, int height, String[] values) {
		super(name, displayName, x, y, width, height);
		this.values = new ArrayList<String>(Arrays.asList(values));
	}


	public UIEDropdown pick(int mouseX, int mouseY) {
		boolean pick = false;

		if (open) {
			if (mouseX > this.x && mouseX < this.x + width && mouseY > this.y && mouseY < this.y + (height * (values.size() + 1))) {
				int id = (int) (1.0 * (mouseY - y - height) / height);
				if (id < 0)
					return (null);
				selectedValue = id;
				open = !open;
				pick = true;
				execute();
			}
		}

		if (super.pick(mouseX, mouseY) == this) {
			open = !open;
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
	public void render() {
		Graphics2D.text(x + displayX, y + displayY, displayName, true);

		Graphics2D.rect(x, y, width, height, Color3i.white, selected ? Color3i.blue : Color3i.black);

		if (open) {
			for (int i = 0; i < values.size(); i++) {
				int yPos = y + (height * (i + 1));
				Graphics2D.rect(x, yPos, width, height, new Color3i(220, 220, 220), selected ? Color3i.blue : Color3i.black);
				Graphics2D.text(x + 3, yPos + 5, values.get(i), true);
			}
		}

		Graphics2D.text(x+3, y+5, values.get(selectedValue), true);

		super.render();
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
}