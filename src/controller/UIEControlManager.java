package controller;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;


/**
 * Manages a set of interface elements, handles selection between them and
 * redirects keyboard input to them
 *
 */
public class UIEControlManager {

	public UserInterfaceElement<? extends UserInterfaceElement<?>> keyboardTarget = null;

	private ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>> allElements;
	private ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>> currentLayer;
	
	private UIEScrollbar scrollbar;

	int positionX;
	int positionY;
	
	private int width;
	private int height;

	private int currentX;
	private int currentY;

	private int topGutter = 50;
	private int leftGutter = 10;

	private int gutterX = 10;
	private int gutterY = 10;
	
	private boolean useScrollbar = false;


	public UIEControlManager(int positionX, int positionY, int width, int height, int leftGutter, int topGutter, int gutterX, int gutterY, boolean useScrollbar) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.width = width;
		this.height = height;
		this.currentX = leftGutter;
		this.currentY = topGutter;

		this.topGutter = topGutter;
		this.leftGutter = leftGutter;
		this.gutterX = gutterX;
		this.gutterY = gutterY;

		this.allElements = new ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>>();
		this.currentLayer = new ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>>();
		
		this.scrollbar = new UIEScrollbar("scrollbar", "Scrollbar", this.width - 20, 20, 20, this.height, -1, -1);
		
		this.useScrollbar = useScrollbar;
	}


	public void setCurrentY(int y) {
		this.currentY = y;
	}


	public void add(UserInterfaceElement<? extends UserInterfaceElement<?>> uie) {
		
		if (uie.width == -1 || uie.fullWidth) {
			uie.fullWidth = true;
			uie.setWidth(this.width - (leftGutter * 2) - scrollbar.width);
		}

		if (currentX + uie.getLayoutWidth() > width - (leftGutter + scrollbar.width)) {
			newLine(false);
		}

		uie.setPosition(currentX, currentY);
		uie.reflow();

		currentLayer.add(uie);

		currentX += uie.getLayoutWidth();
		currentX += gutterX;
		
		if (this.currentY + uie.getLayoutHeight() > this.height) {
			scrollbar.visible = true;
			
			scrollbar.setVisibleArea(this.height);
			scrollbar.setItemCount(this.currentY + uie.getLayoutHeight());
		}

	}


	public void render() {
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glTranslated(positionX, positionY, 0);
		GL11.glTranslated(0, -scrollbar.positionItems, 0);
	
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : allElements) {
			uie.render();
		}
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
	

		if (useScrollbar) {
			scrollbar.render();
		}
	}


	public void keyPressed(int key) {
		if (keyboardTarget != null) {
			if (key == GLFW.GLFW_KEY_TAB) {
				keyboardTarget.execute();
				keyboardTarget.selected = false;
				int id = allElements.indexOf(keyboardTarget);
				id++;
				if (id >= allElements.size())
					id = 0;
				keyboardTarget = allElements.get(id);
				keyboardTarget.selected = true;
			}
			keyboardTarget.keyPressed(key);
		}
	}
	
	
	public void mousePressed(int mouseX, int mouseY) {
		keyboardTarget = null;
		mouseX -= this.positionX;
		mouseY -= this.positionY;
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : allElements) {
			UserInterfaceElement<? extends UserInterfaceElement<?>> pickResult = uie.pick(mouseX, mouseY + scrollbar.positionItems);

			if (pickResult != null) {
				if (pickResult instanceof UIETextField || pickResult instanceof UIETerminal) {
					keyboardTarget = pickResult;
				}
			}
		}
		
		scrollbar.pick(mouseX, mouseY);
	}


	public void mouseDragged(int mouseButton, int mouseX, int mouseY, int dx, int dy) {
		mouseX -= this.positionX;
		mouseY -= this.positionY;
		
		
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : allElements) {
			uie.mouseDragged(mouseX, mouseY + scrollbar.positionItems, dx, dy);
		}
		
		scrollbar.mouseDragged(mouseX, mouseY, dx, dy);
	}


	public void mouseReleased() {
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : allElements) {
			uie.mouseReleased();
		}
		
		scrollbar.mouseReleased();
	}
	
	
	public void mouseWheel(int mouseX, int mouseY, int delta) {
		mouseX -= this.positionX;
		mouseY -= this.positionY;
		boolean scrolledElement = false;
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : allElements) {
			if (uie.pick(mouseX, mouseY + scrollbar.positionItems) != null) {
				uie.mouseWheel(delta);	
				scrolledElement = true;
				break;
			}
		}
		if (scrolledElement == false) {
			scrollbar.mouseWheel(delta);	
		}
	}


	public void textInput(char character) {
		if (keyboardTarget != null) {
			keyboardTarget.textInput(character);
		}
	}
	
	
	public void setKeyboardTarget(UserInterfaceElement<? extends UserInterfaceElement<?>> c) {
		keyboardTarget = c;
	}


	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}


	public void newLine() {
		newLine(true);
	}
	
	
	/**
	 * The next component added will start at the left gutter. Can be requested manually (explicitly) or during the add step if an overflow would occur
	 * @param explicit
	 */
	public void newLine(boolean explicit) {
		currentX = leftGutter;

		int maxHeight = -1;
		for (UserInterfaceElement<? extends UserInterfaceElement<?>> uie : currentLayer) {
			if (uie.getHeight() > maxHeight) {
				maxHeight = uie.getLayoutHeight();
			}
		}
		currentY += maxHeight;
		currentY += gutterY;

		currentLayer.add(new UIENewLine(explicit));

		allElements.addAll(currentLayer);
		currentLayer.clear();
	}


	public void finalizeLayer() {
		allElements.addAll(currentLayer);
		currentLayer.clear();
	}


	public void reflow() {
		
		this.scrollbar.x = this.width - scrollbar.width;
		scrollbar.height = this.height;
		scrollbar.setItemCount(-1);
		scrollbar.setVisibleArea(this.height);
		
		ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>> listCopy = new ArrayList<UserInterfaceElement<? extends UserInterfaceElement<?>>>(allElements);
		allElements.clear();
		currentLayer.clear();

		this.currentX = leftGutter;
		this.currentY = topGutter;
		
		scrollbar.visible = false;

		for (UserInterfaceElement<? extends UserInterfaceElement<?>> e : listCopy) {
			if (e instanceof UIENewLine) {
				UIENewLine newline = (UIENewLine) e;
				if (newline.explicit) {
					newLine(true);	
				}
			}
			else {
				add(e);
			}
		}
		
		if (scrollbar.visible == false) {
			scrollbar.setScrollPosition(0);
		}

		finalizeLayer();
	}
}
