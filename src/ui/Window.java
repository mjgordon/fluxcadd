package ui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import fonts.PointFont;
import utility.Util;

import static org.lwjgl.opengl.GL11.*;
public class Window {
	public int x;
	public int y;
	public int width;
	public int height;
	
	public boolean resizing = false;
	public int resizeX;
	public int resizeY;
	public int resizeWidth = -1;
	public  int resizeHeight = -1;
	
	public int backgroundColor;
	public int borderColor;
	public int barColor;
	public int fontColor = 0xFFFFFFFF;
	
	public int barHeight = 20;
	
	public WindowContent content;
	
	public String windowTitle = "";
	
	public boolean closeable = true;
	public boolean moveable = true;
	public boolean showBar = true;
	public boolean resizable = true;
	
	public Window(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.backgroundColor = 0xFFA0A0A0;
		this.borderColor = 0xFFFFFFFF;
		this.barColor = 0xFF404040;
	}
	public Window(int x, int y, int width, int height, int backgroundColor, int borderColor, int barColor) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.barColor = barColor;	
	}
	
	public Window(String preset) {
		if (preset.equals("terminal")) {
			x = 0;
			y = 0;
			width = Display.getWidth() - 1;
			height = 60;
			this.backgroundColor = 0xFF404040;
			this.borderColor = 0xFFFFFFFF;
			this.barColor = 0xFF404040;
			closeable = false;
			moveable = false;
			showBar = false;
			resizable = false;
			content = new Content_Terminal(this);
		}
	
	}
	
	public void render() {
		
		//Background
		Util.fill(backgroundColor);
		Util.noStroke();
		Util.rect(x,y,width,height);	
		
		//Content of the window
		if (content != null) {
			content.render();
		}
				
		if (showBar){
			//Bar
			Util.fill(barColor);
			Util.noStroke();
			Util.rect(x,y+height-barHeight, width, barHeight);
			
			//Window Title
			glColor3f(1,1,1);
			PointFont.drawString(windowTitle, x + 5, y + height - 15);
		}
			
		//Resizing ghost
		if (resizing) {
			Util.noFill();
			Util.stroke(0xFFFFFF00);
			Util.rect(resizeX,resizeY,resizeWidth, - resizeHeight);
		}
		
		//Border
		Util.noFill();
		Util.stroke(borderColor);
		Util.rect(x,y,width,height);

		//Resizer
		if (resizable) {
			Util.line(x + width - 10, y, x + width - 10, y + 10);
			Util.line(x + width - 10, y + 10, x + width, y + 10);
		}
		
		
		//Close Button
		if (closeable && showBar) {
			Util.rect(x + width - 15, y + height - 15, 10,10);
			Util.line(x + width - 15, y + height - 5, x + width - 5, y + height - 15);
			Util.line(x + width - 5, y + height - 5, x + width - 15, y + height - 15);
		}
		

		
		
	}
	
	public boolean pick() {
		return(Mouse.getX() > x &&
			   Mouse.getY() > y &&
			   Mouse.getX() < x + width &&
			   Mouse.getY() < y + height);
	}
	
	public boolean pickBar() {
		if (!moveable || !showBar) return(false);
		return(Mouse.getX() > x &&
			   Mouse.getY() > y + height - barHeight &&
			   Mouse.getX() < x + width &&
			   Mouse.getY() < y + height);
	}
	public boolean pickResize() {
		if (!resizable) return(false);
		return(Mouse.getX() > x + width - 10 &&
			   Mouse.getY() > y &&
			   Mouse.getX() < x + width &&
			   Mouse.getY() < y + 10);
		
	}
	public boolean pickClose() {
		if (!closeable) return(false);
		return(Mouse.getX() > x + width - 15 &&
			   Mouse.getY() > y + height - 15 &&
			   Mouse.getX() < x + width - 5 &&
			   Mouse.getY() < y + height - 5);
	}
	
	public void startResize(int newWidth, int newHeight) {
		resizeX = x;
		resizeY = y+height;
		resizeWidth = newWidth;
		resizeHeight = newHeight;
		if (resizeWidth < 100) resizeWidth = 100;
		if (resizeHeight < 100) resizeHeight = 100;
		resizing = true;
	}
	
	public void endResize() {
		y -= resizeHeight - height;
		width = resizeWidth;
		height = resizeHeight;
		
		if (height < 100) height = 100;
		if (width < 100) width = 100;
		if (height > Display.getHeight()) height = Display.getHeight();
		resizeWidth = -1;
		resizeHeight = -1;
		resizing = false;
	}
	
	
	public void move(int dx, int dy) {
		x += dx;
		y += dy;
		if (x + width - 20 < 0) x = -width + 20;
		if (x > Display.getWidth() -20 ) x = Display.getWidth() - 20;
		
		if (y + height - barHeight < 0) y = -height + barHeight;
		else if (y + height > Display.getHeight()) y = Display.getHeight() - height;
	}
	
	public void mousePressed() {
		content.mousePressed();
	}

	public void mouseDragged() {
		content.mouseDragged();
	}
}
