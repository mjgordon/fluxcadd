package ui;

import main.Config;
import main.FluxCadd;
import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;
import utility.Color3i;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

/**
 * Panels are subwindows within the main OS window They each contain a Content
 * object, such as UI or 3d view
 */
public final class Panel {
	protected int positionX;
	protected int positionY;
	protected int width;
	protected int height;
	
	/**
	 * While dragging a panel split border, stores the width before any dragging occurred
	 */
	protected int predragWidth;
	
	/**
	 * While dragging a panel split border, stores the height before any dragging occurred
	 */
	protected int predragHeight;

	protected int minimumWidth = 10;
	protected int minimumHeight = 10;

	protected int maximumWidth = -1;
	protected int maximumHeight = -1;
	

	public int backgroundColor;
	public int borderColor;
	public int barColor;
	public int fontColor = 0xFFFFFFFF;

	public int barHeight = 20;

	public Content content;

	public String windowTitle = "";

	public boolean showBar = true;

	protected ArrayList<Panel> children;

	/**
	 * -1 : No resizing occurring
	 *  N : The split line after child N is being dragged
	 */
	protected int resizeSelected = -1;

	/**
	 * Stores the relative size of each child panel (array sums to 1) Stored
	 * separately as multiplying integers loses information over time
	 */
	private ArrayList<Double> childrenRatios = new ArrayList<Double>();

	/**
	 * If SINGLE, panel will contains its own content instance. Otherwise, panel
	 * will contain 2 or more child panel instances
	 */
	protected SplitState splitState = SplitState.SINGLE;

	/**
	 * Number of pixels from the resizable border the mouse cursor can be
	 */
	private static final int resizeGutter = 3;


	/**
	 * HORIZONTAL and VERTICAL refer to the arrangement of the child windows in
	 * relation to each other, not to the direction of the split line
	 */
	public enum SplitState {
		SINGLE,
		HORIZONTAL,
		VERTICAL
	}


	public Panel() {
		this.positionX = 0;
		this.positionY = 0;
		this.width = 10;
		this.height = 10;
		this.predragWidth = 10;
		this.predragHeight = 10;

		this.backgroundColor = Config.getInt("ui.color.background.ui", 16);
		this.borderColor = 0xFFFFFFFF;
		this.barColor = 0xFF404040;

		children = new ArrayList<Panel>();
	}


	public Panel(int x, int y, int width, int height) {
		this.positionX = x;
		this.positionY = y;
		this.width = width;
		this.height = height;
		this.predragWidth = width;
		this.predragHeight = height;

		this.backgroundColor = Config.getInt("ui.color.background.ui", 16);
		this.borderColor = 0xFFFFFFFF;
		this.barColor = 0xFF404040;

		children = new ArrayList<Panel>();
	}


	/**
	 * For panel presets
	 * 
	 * @param preset
	 */
	public Panel(String preset) {
		if (preset.equals("terminal")) {
			this.positionX = 0;
			this.positionY = 0;
			this.width = FluxCadd.backend.getWidth() - 1;
			this.height = 60;
			this.predragWidth = this.width;
			this.predragHeight = this.height;
			this.maximumHeight = 60;
			this.backgroundColor = 0xFF404040;
			this.borderColor = 0xFFFFFFFF;
			this.barColor = 0xFF404040;
			
			showBar = false;
			content = new Content_Terminal(this);
		}

		children = new ArrayList<Panel>();
	}


	/**
	 * Renders a single panel container, and recursively call its children's render
	 * functions if applicable
	 * 
	 * @param selected reference to the currently selected panel, for comparison
	 */
	public void render(Panel selected) {
		if (children.size() > 0) {
			for (Panel panel : children) {
				panel.render(selected);
			}
		}

		else {
			GL11.glPushMatrix();

			GL11.glTranslatef(positionX, positionY, 0);

			// Background
			OGLWrapper.fill(backgroundColor);
			OGLWrapper.noStroke();
			Primitives.rect(0, 0, width, height);

			// Content of the window
			if (content != null) {
				content.render();
			}

			if (showBar) {
				// Bar
				OGLWrapper.fill(barColor);
				OGLWrapper.noStroke();
				Primitives.rect(0, 0, width, barHeight);

				// Window Title
				BitmapFont.drawString(windowTitle, 5, 4, new Color3i(255, 255, 255));
			}

			// Border
			OGLWrapper.noFill();
			OGLWrapper.glLineWidth(1);
			if (selected == this) {
				OGLWrapper.stroke(0, 0, 255);
			}

			else {
				OGLWrapper.stroke(borderColor);
			}

			Primitives.rect(0, 0, width, height);
			// OGLWrapper.stroke(borderColor);

			GL11.glPopMatrix();
		}
	}


	public Panel pick(int mouseX, int mouseY) {
		if (splitState == SplitState.SINGLE) {
			return (mouseX > positionX && mouseY > positionY && mouseX < positionX + width && mouseY < positionY + height) ? this : null;
		}
		else {
			for (Panel p : children) {
				Panel childPick = p.pick(mouseX, mouseY);
				if (childPick != null) {
					return childPick;
				}
			}
		}
		return null;
	}


	public Panel pickBorder(int mouseX, int mouseY) {
		if (splitState == SplitState.SINGLE) {
			resizeSelected = -1;
		} 
		else {
			if (splitState == SplitState.HORIZONTAL) {
				if (mouseY > positionY && mouseY < positionY + height) {
					int currentBorderX = 0;
					for (int i = 0; i < children.size(); i++) {
						currentBorderX += children.get(i).width;
						if (Math.abs(mouseX - positionX - currentBorderX) < resizeGutter) {
							resizeSelected = i;
							return this;
						}
					}
				}
			}
			else if (splitState == SplitState.VERTICAL) {
				if (mouseX > positionX && mouseX < positionX + width) {
					int currentBorderY = 0;
					for (int i = 0; i < children.size(); i++) {
						currentBorderY += children.get(i).height;
						if (Math.abs(mouseY - positionY - currentBorderY) < resizeGutter) {
							resizeSelected = i;
							return this;
						}
					}
				}
			}
			
			resizeSelected = -1;
			
			// If we did not return early above, check each child
			for (Panel p : children) {
				Panel childPick = p.pickBorder(mouseX, mouseY);
				if (childPick != null) {
					return childPick;
				}
			}
		}
		
		return null;
	}


	public void mousePressed(int button, int mouseX, int mouseY) {
		mouseX -= positionX;
		mouseY -= positionY;
		content.mousePressed(button, mouseX, mouseY);
	}


	public void mouseReleased(int button) {
		content.mouseReleased(button);
	}


	public void mouseDragged(int button, int dx, int dy) {
		content.mouseDragged(button, dx, dy);
	}


	/**
	 * Sets a panel to be split rather than content-having, or adds a new split
	 * child
	 * 
	 * @param splitType
	 * @param child2
	 * @return
	 */
	public Panel split(SplitState splitType, Panel child2) {
		splitState = splitType;
		Panel child1;
		if (splitType == SplitState.HORIZONTAL) {
			child1 = new Panel(this.positionX, this.positionY, this.width / 2, this.height);
			child2.positionX = this.positionX + (this.width / 2);
			child2.width = this.width / 2;
			child2.height = this.height;

			if (child1.maximumWidth != -1 && child1.width > child1.maximumWidth) {
				int diff = child1.width - child1.maximumWidth;
				child1.width -= diff;
				child2.width += diff;
				child2.positionX -= diff;
			}

			if (child2.maximumWidth != -1 && child2.width > child2.maximumWidth) {
				int diff = child2.width - child2.maximumWidth;
				child2.width -= diff;
				child1.width += diff;
				child2.positionX += diff;
			}

			childrenRatios.add(1.0 * child1.width / width);
			childrenRatios.add(1.0 * child2.width / width);
		}
		else {
			child1 = new Panel(this.positionX, this.positionY, this.width, this.height / 2);
			child2.positionY = this.positionY + (this.height / 2);
			child2.height = this.height / 2;
			child2.width = this.width;

			if (child1.maximumHeight != -1 && child1.height > child1.maximumHeight) {
				int diff = child1.height - child1.maximumHeight;
				child1.height -= diff;
				child2.height += diff;
				child2.positionY -= diff;
			}

			if (child2.maximumHeight != -1 && child2.height > child2.maximumHeight) {
				int diff = child2.height - child2.maximumHeight;
				child2.height -= diff;
				child1.height += diff;
				child2.positionY += diff;
			}
			childrenRatios.add(1.0 * child1.height / height);
			childrenRatios.add(1.0 * child2.height / height);
		}

		children.add(child1);
		children.add(child2);

		if (this.content != null) {
			child1.content = this.content;
			child1.content.setParent(child1);
		}

		this.content = null;
		child1.windowTitle = this.windowTitle;

		if (child1.content != null) {
			child1.content.resizeRespond(child1.width, child1.height);
		}

		if (child2.content != null) {
			child2.content.resizeRespond(child1.width, child1.height);
		}

		return (this);
	}


	/**
	 * Resizes the split tree based on a new window size
	 */
	public void reflowSplits(int newWidth, int newHeight) {
		switch (splitState) {
		case HORIZONTAL:
			int newX = 0;
			for (int i = 0; i < children.size(); i++) {
				int panelWidth = (int) (childrenRatios.get(i) * newWidth);
				children.get(i).reflowSplits(panelWidth, newHeight);
				children.get(i).positionX = this.positionX + newX;
				children.get(i).positionY = this.positionY;
				newX += panelWidth;
			}
			break;
		case VERTICAL:
			int newY = 0;
			for (int i = 0; i < children.size(); i++) {
				int panelHeight = (int) (childrenRatios.get(i) * newHeight);
				children.get(i).reflowSplits(newWidth, panelHeight);
				children.get(i).positionX = this.positionX;
				children.get(i).positionY = this.positionY + newY;
				newY += panelHeight;
			}
			break;
		case SINGLE:
			content.resizeRespond(newWidth, newHeight);
			break;
		}

		this.width = newWidth;
		this.height = newHeight;
	}


	/**
	 * Prints information about itself and all children recursively
	 * 
	 * @param depth
	 */
	public void printTree(int depth) {
		String out = " ".repeat(depth);
		out += this.getClass() + "(" + this.positionX + "," + this.positionY + ")(" + this.width + "," + this.height + "):";
		out += content + "(" + this.splitState + ")";
		System.out.println(out);
		for (Panel c : children) {
			c.printTree(depth + 1);
		}
	}


	public Panel getChild(int i) {
		if (children == null || children.size() == 0) {
			return (null);
		}
		else {
			return children.get(i);
		}
	}
}
