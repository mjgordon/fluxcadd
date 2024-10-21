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
public class Panel {
	private int positionX;
	private int positionY;
	private int width;
	private int height;

	private int minimumWidth = 10;
	private int minimumHeight = 10;

	public int maximumWidth = -1;
	public int maximumHeight = -1;

	/**
	 * If true, panel boundary is currently being dragged
	 */
	public boolean resizing = false;
	public int resizeX;
	public int resizeY;
	public int resizeWidth = -1;
	public int resizeHeight = -1;

	public int backgroundColor;
	public int borderColor;
	public int barColor;
	public int fontColor = 0xFFFFFFFF;

	public int barHeight = 20;

	public Content content;

	public String windowTitle = "";

	public boolean moveable = true;
	public boolean showBar = true;
	public boolean resizable = true;

	private ArrayList<Panel> children;
	
	/**
	 * Stores the relative size of each child panel (array sums to 1)
	 * Stored separately as multiplying integers loses information over time
	 */
	private ArrayList<Double> childrenRatios = new ArrayList<Double>();

	/**
	 * If SINGLE, panel will contains its own content instance. 
	 * Otherwise, panel will contain 2 or more child panel instances 
	 */
	private SplitState splitState = SplitState.SINGLE;


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
			positionX = 0;
			positionY = 0;
			width = FluxCadd.backend.getWidth() - 1;
			height = 60;
			this.maximumHeight = 60;
			this.backgroundColor = 0xFF404040;
			this.borderColor = 0xFFFFFFFF;
			this.barColor = 0xFF404040;
			moveable = false;
			showBar = false;
			resizable = false;
			content = new Content_Terminal(this);
		}

		children = new ArrayList<Panel>();
	}


	/**
	 * Renders a single panel container, and recursively call its children's render functions if applicable
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

			// Resizing ghost
			if (resizing) {
				OGLWrapper.noFill();
				OGLWrapper.stroke(0xFFFFFF00);
				Primitives.rect(resizeX, resizeY, resizeWidth, -resizeHeight);
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

			// Resizer
			if (resizable) {
				Primitives.line(width - 10, height, width - 10, height - 10);
				Primitives.line(width - 10, height - 10, width, height - 10);
			}

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
		return (null);
	}


	public boolean pickBar(int mouseX, int mouseY) {
		if (!moveable || !showBar)
			return (false);
		return (mouseX > positionX && mouseY > positionY + height - barHeight && mouseX < positionX + width && mouseY < positionY + height);
	}


	public boolean pickResize(int mouseX, int mouseY) {
		if (!resizable)
			return (false);
		return (mouseX > positionX + width - 10 && mouseY > positionY && mouseX < positionX + width && mouseY < positionY + 10);

	}


	public void startResize(int newWidth, int newHeight) {
		resizeX = 0;
		resizeY = height;
		resizeWidth = newWidth;
		resizeHeight = newHeight;
		if (resizeWidth < 100)
			resizeWidth = 100;
		if (resizeHeight < 100)
			resizeHeight = 100;
		resizing = true;
	}


	public void endResize() {
		positionY -= resizeHeight - height;
		width = resizeWidth;
		height = resizeHeight;

		if (height < 100)
			height = 100;
		if (width < 100)
			width = 100;
		if (height > FluxCadd.backend.getHeight())
			height = FluxCadd.backend.getHeight();
		resizeWidth = -1;
		resizeHeight = -1;
		resizing = false;
	}


	public void move(int dx, int dy) {
		positionX += dx;
		positionY += dy;
		if (positionX + width - 20 < 0)
			positionX = -width + 20;
		if (positionX > FluxCadd.backend.getWidth() - 20)
			positionX = FluxCadd.backend.getWidth() - 20;

		if (positionY + height - barHeight < 0)
			positionY = -height + barHeight;
		else if (positionY + height > FluxCadd.backend.getHeight())
			positionY = FluxCadd.backend.getHeight() - height;
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
	 * Sets a panel to be split rather than content-having, or adds a enw split child
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
				int panelWidth = (int)(childrenRatios.get(i) * newWidth);
				children.get(i).reflowSplits(panelWidth, newHeight);
				children.get(i).setX(this.positionX + newX);
				children.get(i).setY(this.positionY);
				newX += panelWidth;
			}
			break;
		case VERTICAL:
			int newY = 0;
			for (int i = 0; i < children.size(); i++) {
				int panelHeight = (int)(childrenRatios.get(i) *  newHeight);
				children.get(i).reflowSplits(newWidth, panelHeight);
				children.get(i).setX(this.positionX);
				children.get(i).setY(this.positionY + newY);
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


	public int getX() {
		return positionX;
	}


	public int getY() {
		return positionY;
	}


	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}


	public void setX(int x) {
		this.positionX = x;
	}


	public void setY(int y) {
		this.positionY = y;
	}


	public void setWidth(int w) {
		width = w;
	}


	public void setHeight(int h) {
		height = h;
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
