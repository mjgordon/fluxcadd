package controller;

import org.lwjgl.glfw.GLFW;

import event.EventListener;
import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;
import utility.math.Domain;

public class UIETextField extends UserInterfaceElement<UIETextField> {

	public String currentString = "";

	private int highlight = 0xFFFFFF;

	private boolean clearOnExecute = true;

	private boolean autoNewline = false;

	private boolean numberField = false;
	private double backingDouble = 0;
	private Domain numberFieldDomain = null;


	public UIETextField(EventListener target, String name, String displayName, int x, int y, int width, int height) {
		super(target, name, displayName, x, y, width, height);
	}


	public UIETextField(EventListener target, String name, String displayName, int x, int y, int width, int height, double backingDouble, Domain numberFieldDomain) {
		super(target, name, displayName, x, y, width, height);
		this.numberField = true;
		this.backingDouble = backingDouble;
		this.numberFieldDomain = numberFieldDomain;
		this.setValueSilent(backingDouble + "");
	}


	public void keyPressed(int key) {
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
			//backingDouble = numberFieldDomain.clip(backingDouble + (dx * (numberFieldDomain.getSize() * 0.01)));
			backingDouble = numberFieldDomain.clip(backingDouble + dx);
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
		String renderedString = currentString;
		int estimatedWidth = BitmapFont.cellWidth * renderedString.length();
		if (estimatedWidth > width) {
			if (autoNewline) {
				System.out.println("UIETextField autonewline not implemented!");
			}
			else {
				int acceptedCharLength = width / BitmapFont.cellWidth;
				renderedString = renderedString.substring(0, acceptedCharLength);
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
		BitmapFont.drawString(renderedString, x + 3, y + 5, null);
		BitmapFont.drawString(displayName, x + displayX, y + displayY, null);

		super.render();
	}


	public UIETextField setClearOnExecute(boolean clear) {
		this.clearOnExecute = clear;
		return (this);
	}

}
