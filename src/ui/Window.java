package ui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import fonts.PointFont;
import utility.Util;
import static org.lwjgl.opengl.GL11.*;

public class Window {
	private int x;
	private int y;
	private int width;
	private int height;
	
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
	
	public void render(boolean selected) {
		glPushMatrix();
		glTranslatef(x,y,0);
		
		//Background
		Util.fill(backgroundColor);
		Util.noStroke();
		Util.rect(0,0,width,height);	
		
		//Content of the window
		if (content != null) {
			content.render();
		}
				
		if (showBar){
			//Bar
			Util.fill(barColor);
			Util.noStroke();
			Util.rect(0,height-barHeight, width, barHeight);
			
			//Window Title
			glColor3f(1,1,1);
			PointFont.drawString(windowTitle, 5,height - 15);
		}
			
		//Resizing ghost
		if (resizing) {
			Util.noFill();
			Util.stroke(0xFFFFFF00);
			Util.rect(resizeX,resizeY,resizeWidth, - resizeHeight);
		}
		
		//Border
		Util.noFill();
		if (selected) Util.stroke(0,0,255);
		else Util.stroke(borderColor);
		Util.rect(0,0,width,height);
		Util.stroke(borderColor);

		//Resizer
		if (resizable) {
			Util.line(width - 10, 0,width - 10,10);
			Util.line(width - 10, 0 + 10,width,10);
		}
		
		
		
		//Close Button
		if (closeable && showBar) {
			Util.rect(width - 15, height - 15, 10,10);
			Util.line(width - 15, height - 5,width - 5, height - 15);
			Util.line(width - 5, height - 5,width - 15, height - 15);
		}
		
		glPopMatrix();
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
		resizeX = 0;
		resizeY = height;
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
	
	public int getX() {
		return(x);
	}
	
	public int getY() {
		return(y);
	}
	
	public int getWidth() {
		return(width);
	}
	
	public int getHeight() {
		return(height);
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
	
}
