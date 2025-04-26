package controller;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.glfw.GLFW;

import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;
import utility.math.Domain;

public class UIETextField extends UserInterfaceElement<UIETextField> {

	/**
	 * The editable flag controls two behaviours
	 * - With the element selected, if true the user can enter text
	 * - If false, individual lines can be selected
	 */
	public boolean editable = true;
	
	private ArrayList<String> currentLines = new ArrayList<String>(Arrays.asList(new String[] {""}));

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
	
	/**
	 * Line offset
	 */
	private int offset = 0;
	
	private UIEScrollbar scrollbar;
	
	private boolean scrollable = false;


	public UIETextField(String name, String displayName, int x, int y, int width, int height) {
		super(name, displayName, x, y, width, height);
		scrollable = false;
		scrollbar = new UIEScrollbar("scrollbar", "", this.x + this.width - 20, this.y, 20, this.height, -1, -1);
		scrollbar.setVisibleArea(height / 12);
	}
	
	public UIETextField(String name, String displayName, int x, int y, int width, int height, boolean scrollable) {
		super(name, displayName, x, y, width, height);
		this.scrollable = scrollable;
		scrollbar = new UIEScrollbar("scrollbar", "", this.x + this.width - 20, this.y, 20, this.height, -1, -1);
		scrollbar.setVisibleArea(height / 12);
	}


	public UIETextField(String name, String displayName, int x, int y, int width, int height, double backingDouble, Domain numberFieldDomain,double numberFieldDelta) {
		super(name, displayName, x, y, width, height);
		
		scrollable = false;
		scrollbar = new UIEScrollbar("scrollbar", "", this.x + this.width - 20, this.y, 20, this.height, -1, -1);
		scrollbar.setVisibleArea(height / 12);
		
		this.numberField = true;
		this.backingDouble = backingDouble;
		this.numberFieldDomain = numberFieldDomain;
		this.setValue(backingDouble + "", true);
		this.numberFieldDelta = numberFieldDelta;
		this.clearOnExecute = false;
	}
	
	
	@Override
	public UIETextField pick(int mouseX, int mouseY) {
		
		scrollbar.pick(mouseX, mouseY);
		
		if (super.pick(mouseX, mouseY) == this) {
			if (!editable) {
				mouseY -= this.y;
				mouseY -= gutterY;
				selectedLine = mouseY / BitmapFont.cellHeight + offset;
				
				execute();	
			}
			return this;
		}
		return null;
	}


	@Override
	public void keyPressed(int key) {
		if (!editable) {
			return;
		}
		
		if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
			execute();
		}
		else if (key == GLFW.GLFW_KEY_BACKSPACE) {
			backspace();	
		}
	}
		
	
	@Override
	public void mouseWheel(int delta) {
		if (scrollbar.active) {
			scrollbar.mouseWheel(delta);
			this.offset = scrollbar.positionItems;	
		}
		
	}


	@Override
	public void textInput(char character) {
		if (!editable) {
			return;
		}
		
		if (currentLines.size() == 0) {
			currentLines.add("");
		}
		
		if (Character.isLetterOrDigit(character)) {
			currentLines.set(currentLines.size() - 1, lastLine() + character);
		}
	}
	
	
	@Override
	public void setWidth(int width) {
		super.setWidth(width);
		scrollbar.setPosition(this.x + this.width - scrollbar.width, this.y);
	}


	public void setValue(String s, boolean silent) {
		this.currentLines = new ArrayList<String>(Arrays.asList(s.split("\n")));
		if (scrollable) {
			scrollbar.setItemCount(this.currentLines.size());	
		}
		if (!silent) {
			execute();
		}
	}


	public String getValue() {
		return (this.lastLine());
	}
	

	public double getBackingDouble() {
		return (this.backingDouble);
	}


	@Override
	public void mouseDragged(int x, int y, int dx, int dy) {
		if (selected) {
			if (numberField) {
				backingDouble = numberFieldDomain.clip(backingDouble + (dx * numberFieldDelta));
				setValue(backingDouble + "", false);	
			}
			else if (scrollbar.active) {
				scrollbar.mouseDragged(x, y, dx, dy);
				this.offset = scrollbar.positionItems;	
			}
		}
	}


	@Override
	public void execute() {
		super.execute();

		if (clearOnExecute) {
			this.currentLines = new ArrayList<String>(Arrays.asList(new String[] {""}));
		}
	}


	@Override
	protected void render() {
		
		int maxLines = getHeight() / BitmapFont.cellHeight - 1 + 1;

		OGLWrapper.fill(255, 255, 255);
		if (selected) {
			OGLWrapper.stroke(0, 0, 255);
		}
		else {
			OGLWrapper.stroke(0, 0, 0);
		}
		Primitives.rect(x, y, width, height);

		
		int selectedLineOffset = selectedLine - offset;
		if (selectedLineOffset >= 0 && selectedLineOffset <= maxLines) {
			OGLWrapper.noStroke();
			OGLWrapper.fill(200,200,200);
			Primitives.rect(x + 1, y + gutterY - 1 + (selectedLineOffset * BitmapFont.cellHeight), width - 3,BitmapFont.cellHeight - 1);
		}
		
		OGLWrapper.glColor(0, 0, 0);
		int lineY = y + gutterY;
		
		int acceptedCharLength = width / BitmapFont.cellWidth;
		
		if (acceptedCharLength < 0) {
			return;
		}
		
		for (int i = 0; i < maxLines; i++) {
			int n = i + offset;
			if (n < 0 || n >= currentLines.size()) {
				continue;
			}
			
			String sTemp = currentLines.get(n);
			if (sTemp.length() > acceptedCharLength) {
				sTemp = sTemp.substring(0,acceptedCharLength);
			}
			BitmapFont.drawString(sTemp, x + 3, lineY, null);
			lineY += BitmapFont.cellHeight;
		}
		
		BitmapFont.drawString(displayName, x + displayX, y + displayY, null);
		
		scrollbar.render();

		super.render();
	}


	public UIETextField setClearOnExecute(boolean clear) {
		this.clearOnExecute = clear;
		return (this);
	}
	
	
	public int getSelectedLine() {
		return(selectedLine);
	}

	
	private String lastLine() {
		if (currentLines.size() == 0) {
			return null;
		}
		else {
			return currentLines.get(currentLines.size() - 1);	
		}
	}
	
	
	private void backspace() {
		if (currentLines.size() > 0) {
			String s = lastLine();
			if (s.length() == 0) {
				dropLine();
			}
			else {
				currentLines.set(currentLines.size() - 1,s.substring(0,s.length() - 1));
			}
		}
	}
	
	
	private void dropLine() {
		if (currentLines.size() > 0) {
			currentLines.remove(currentLines.size() - 1);	
		}
	}
}
