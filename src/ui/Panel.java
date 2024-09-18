package ui;

import main.Config;
import main.FluxCadd;
import fonts.BitmapFont;
import graphics.OGLWrapper;
import graphics.Primitives;
import utility.Color;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

/**
 * Panels are subwindows within the main OS window They each contain a Content
 * object, such as UI or 3d view
 */
public class Panel {
	private int x;
	private int y;
	private int width;
	private int height;

	private int minimumWidth = 10;
	private int minimumHeight = 10;

	public int maximumWidth = -1;
	public int maximumHeight = -1;

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

	private SplitState splitState = SplitState.SINGLE;


	public enum SplitState {
		SINGLE,
		HORIZONTAL,
		VERTICAL
	}


	public Panel() {
		this.x = 0;
		this.y = 0;
		this.width = 10;
		this.height = 10;

		this.backgroundColor = Config.getInt("ui.color.background.ui", 16);
		this.borderColor = 0xFFFFFFFF;
		this.barColor = 0xFF404040;

		children = new ArrayList<Panel>();
	}


	public Panel(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
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
			x = 0;
			y = 0;
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


	public void render(Panel selected) {
		if (children.size() > 0) {
			for (Panel panel : children) {
				panel.render(selected);
			}
		}

		else {
			GL11.glPushMatrix();

			GL11.glTranslatef(x, y, 0);

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
				BitmapFont.drawString(windowTitle, 5, 4, new Color(255, 255, 255));
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
			return (mouseX > x && mouseY > y && mouseX < x + width && mouseY < y + height) ? this : null;
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
		return (mouseX > x && mouseY > y + height - barHeight && mouseX < x + width && mouseY < y + height);
	}


	public boolean pickResize(int mouseX, int mouseY) {
		if (!resizable)
			return (false);
		return (mouseX > x + width - 10 && mouseY > y && mouseX < x + width && mouseY < y + 10);

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
		y -= resizeHeight - height;
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
		x += dx;
		y += dy;
		if (x + width - 20 < 0)
			x = -width + 20;
		if (x > FluxCadd.backend.getWidth() - 20)
			x = FluxCadd.backend.getWidth() - 20;

		if (y + height - barHeight < 0)
			y = -height + barHeight;
		else if (y + height > FluxCadd.backend.getHeight())
			y = FluxCadd.backend.getHeight() - height;
	}


	public void mousePressed(int button, int mouseX, int mouseY) {
		mouseX -= x;
		mouseY -= y;
		content.mousePressed(button, mouseX, mouseY);
	}


	public void mouseReleased(int button) {
		content.mouseReleased(button);
	}


	public void mouseDragged(int button, int dx, int dy) {
		content.mouseDragged(button, dx, dy);
	}


	public Panel split(SplitState splitType, Panel child2) {
		splitState = splitType;
		Panel child1;
		if (splitType == SplitState.VERTICAL) {
			child1 = new Panel(this.x, this.y, this.width / 2, this.height);
			child2.x = this.x + (this.width / 2);
			child2.width = this.width / 2;
			child2.height = this.height;

			if (child1.maximumWidth != -1 && child1.width > child1.maximumWidth) {
				int diff = child1.width - child1.maximumWidth;
				child1.width -= diff;
				child2.width += diff;
				child2.x -= diff;
			}

			if (child2.maximumWidth != -1 && child2.width > child2.maximumWidth) {
				int diff = child2.width - child2.maximumWidth;
				child2.width -= diff;
				child1.width += diff;
				child2.x += diff;
			}
		}
		else {
			child1 = new Panel(this.x, this.y, this.width, this.height / 2);
			child2.y = this.y + (this.height / 2);
			child2.height = this.height / 2;
			child2.width = this.width;

			if (child1.maximumHeight != -1 && child1.height > child1.maximumHeight) {
				int diff = child1.height - child1.maximumHeight;
				child1.height -= diff;
				child2.height += diff;
				child2.y -= diff;
			}

			if (child2.maximumHeight != -1 && child2.height > child2.maximumHeight) {
				int diff = child2.height - child2.maximumHeight;
				child2.height -= diff;
				child1.height += diff;
				child2.y += diff;
			}
		}

		children.add(child1);
		children.add(child2);

		child1.content = this.content;
		child1.content.setParent(child1);
		this.content = null;
		child1.windowTitle = this.windowTitle;

		child1.content.resizeRespond();
		child2.content.resizeRespond();

		return (this);
	}


	/**
	 * Resizes the split tree based on a new window size
	 */
	public void reflowSplits() {
	}


	/**
	 * Prints information about itself and all children recursively
	 * 
	 * @param depth
	 */
	public void printTree(int depth) {
		String out = " ".repeat(depth);
		out += this.getClass() + "(" + this.x + "," + this.y + ")(" + this.width + "," + this.height + "):";
		out += content + "(" + this.splitState + ")";
		System.out.println(out);
		for (Panel c : children) {
			c.printTree(depth + 1);
		}
	}


	public int getX() {
		return (x);
	}


	public int getY() {
		return (y);
	}


	public int getWidth() {
		return (width);
	}


	public int getHeight() {
		return (height);
	}


	public void setX(int x) {
		this.x = x;
	}


	public void setY(int y) {
		this.y = y;
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
