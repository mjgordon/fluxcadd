package controller;

import org.lwjgl.glfw.GLFW;

import event.EventListener;
import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;
import utility.math.Domain;

public class UIETextField extends UserInterfaceElement<UIETextField> {
	
	public boolean editable = true;
	
	private String currentString = "";
	
	private int highlight = 0xFFFFFF;

	private boolean clearOnExecute = true;
	
	private int selectedLine = -1;
	
	private int gutterY = 5;

	
	/**
	 * Special type of textfield, only displays numbers and allows for dragging to change
	 */
	private boolean numberField = false;
	
	/**
	 * If a numberField, backingDouble is the actual numeric value stored and returned
	 */
	private double backingDouble = 0;
	
	/**
	 * If a numberField, numberFieldDomain limits the range the field can represent
	 */
	private Domain numberFieldDomain = null;
	
	/**
	 * If a numberfield, numberFieldDelta is the amount to change for every pixel dragged
	 */
	private double numberFieldDelta = 1;


	public UIETextField(EventListener target, String name, String displayName, int x, int y, int width, int height) {
		super(target, name, displayName, x, y, width, height);
	}


	public UIETextField(EventListener target, String name, String displayName, int x, int y, int width, int height, double backingDouble, Domain numberFieldDomain,double numberFieldDelta) {
		super(target, name, displayName, x, y, width, height);
		this.numberField = true;
		this.backingDouble = backingDouble;
		this.numberFieldDomain = numberFieldDomain;
		this.setValueSilent(backingDouble + "");
		this.numberFieldDelta = numberFieldDelta;
	}
	
	
	@Override
	public UIETextField pick(int mouseX, int mouseY) {
		if (super.pick(mouseX, mouseY) == this) {
			mouseY -= this.y;
			mouseY -= gutterY;
			selectedLine = mouseY / BitmapFont.cellHeight;
			
			execute();
			return (this);
		}
		return (null);
	}


	public void keyPressed(int key) {
		if (!editable) {
			return;
		}
		
		if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
			execute();
		}
		else if (key == GLFW.GLFW_KEY_BACKSPACE) {
			if (currentString.length() > 0)
				currentString = currentString.substring(0, currentString.length() - 1);
		}
	}


	@Override
	public void textInput(char character) {
		if (!editable) {
			return;
		}
		
		if (Character.isLetterOrDigit(character)) {
			currentString += character;
		}
	}


	public void setValue(String s) {
		this.currentString = s;
		execute();
	}


	public void setValueSilent(String s) {
		this.currentString = s;
	}


	public String getValue() {
		return (this.currentString);
	}


	public double getBackingDouble() {
		return (this.backingDouble);
	}


	@Override
	public void mouseDragged(int dx, int dy) {
		if (numberField && selected) {
			backingDouble = numberFieldDomain.clip(backingDouble + (dx * numberFieldDelta));
			setValue(backingDouble + "");
		}
	}


	@Override
	public void execute() {
		super.execute();

		if (clearOnExecute) {
			this.currentString = "";
		}
	}


	@Override
	protected void render() {
		String[] lines = currentString.split("\n");
		
		for (int i = 0; i < lines.length; i++) {
			int lineWidth = BitmapFont.cellWidth * lines[i].length();
			if (lineWidth > width) {
				int acceptedCharLength = width / BitmapFont.cellWidth;
				lines[i] = lines[i].substring(0,acceptedCharLength);
			}
		}

		OGLWrapper.fill(255, 255, 255);
		if (selected) {
			OGLWrapper.stroke(0, 0, 255);
		}
		else {
			OGLWrapper.stroke(0, 0, 0);
		}
		Primitives.rect(x, y, width, height);

		OGLWrapper.noFill();
		OGLWrapper.stroke(highlight);
		Primitives.rect(x + 1, y + 1, width - 2, height - 2);

		OGLWrapper.glColor(0, 0, 0);
		
		int lineY = y + gutterY;
		for (int i = 0; i < lines.length; i++) {
			BitmapFont.drawString(lines[i], x + 3, lineY, null);
			lineY += BitmapFont.cellHeight;
		}
		
		BitmapFont.drawString(displayName, x + displayX, y + displayY, null);

		super.render();
	}


	public UIETextField setClearOnExecute(boolean clear) {
		this.clearOnExecute = clear;
		return (this);
	}
	
	public int getSelectedLine() {
		return(selectedLine);
	}

}
