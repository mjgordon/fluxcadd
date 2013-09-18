package main;

import java.util.ArrayList;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import data.GeometryFile;


import ui.ViewType;
import ui.Window;
import ui.Content_View;
import static org.lwjgl.opengl.GL11.*;

public class Param0 {
	
	public Window terminal;
	
	public ArrayList<Window> windows;
	
	public Window heldWindow = null;
	public Window resizingWindow = null;
	public Window draggedWindow = null;
	
	public int lastPressX = -1;
	public int lastPressY = -1;
	
	public int terminalHeight = 60;
	
	public static GeometryFile geometry;
		
	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		windows = new ArrayList<Window>();
		
		terminal = new Window("terminal");
		windows.add(terminal);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 800, 0, 600, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		Window window = new Window(100,100,300,300);
		window.content = new Content_View(window, ViewType.PERSP);
		windows.add(window);
		
		Window window2 = new Window(450,100,300,300);
		window2.content = new Content_View(window2,ViewType.TOP);
		windows.add(window2);
		
		glClearColor(0.4f,0.4f,1,1);
		
		geometry = new GeometryFile("scripts/test.pl");
		
		//Main program loop
		while (!Display.isCloseRequested()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
			while(Mouse.next()) parseMouse();
			while(Keyboard.next()) parseKeyboard();
			
			for (int i = 0; i < windows.size(); i++) {
				windows.get(windows.size()-i -1).render();
			}

			Display.update();
		}
		
		Display.destroy();
	}
	
	public void parseMouse() {
		if (Mouse.getEventButtonState() == true) mousePressed();
		
		else if (Mouse.getEventDX() != 0 || Mouse.getEventDY() != 0) {
			if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) mouseDragged();
		}
		else if (Mouse.getEventDWheel() != 0) {
			mouseWheel();
		}
		else if (Mouse.getEventButtonState() == false) {
			if (Math.abs(Mouse.getX()-lastPressX) == 0 || Math.abs(Mouse.getY() - lastPressY) ==0) mouseClicked();
			mouseReleased();
		}
		
		
	}
	
	private void mousePressed() {
		lastPressX = Mouse.getX();
		lastPressY = Mouse.getY();
		
		Window popWindow = null;
		for (Window w : windows) {
			if (w.pick()) {
				if (w.pickBar()) heldWindow = w;
				else if (w.pickResize()) resizingWindow = w;
				else draggedWindow = w;
				popWindow = w;
				w.mousePressed();
				break;
			}
			
		}
		if (popWindow != null) {
			windows.remove(popWindow);
			windows.add(0,popWindow);
		}
	}
	
	private void mouseReleased() {
		if (heldWindow != null) {
			checkEdges(true);
			heldWindow.resizing = false;
			heldWindow = null;
		}
		if (resizingWindow != null) {
			resizingWindow.endResize();
			resizingWindow = null;
		}
		
		if (draggedWindow != null) {
			draggedWindow = null;
		}
	}
	
	private void mouseDragged() {
		if (heldWindow != null) {
			int dx = Mouse.getDX();
			int dy = Mouse.getDY();
			checkEdges(false);
			heldWindow.move(dx,dy);
		}
		else if (resizingWindow != null) {
			int newX = Mouse.getX() - resizingWindow.x;
			int newY = (resizingWindow.y + resizingWindow.height) - Mouse.getY();
			
			resizingWindow.startResize(newX,newY);
		}
		else if (draggedWindow != null) {
			draggedWindow.mouseDragged();
		}
	}
	
	private void mouseClicked() {
		Window closedWindow = null;
		for (Window w : windows) {
			if (w.pickClose()) closedWindow = w;
		}
		if (closedWindow != null) windows.remove(closedWindow);
	}
	
	private void mouseWheel() {
		windows.get(0).content.mouseWheel(Mouse.getEventDWheel()/-12);
	}
	
	public void parseKeyboard() {
		if (Keyboard.getEventKeyState() == true) windows.get(0).content.keyPressed();
	}
	
	public void checkEdges(boolean released) {
		//Left Side
		if (Mouse.getX() < 10) {
			//Upper Left Corner
			if (Mouse.getY() > Display.getHeight() - 10) {
				if (released) {
					heldWindow.x = 0;
					heldWindow.y = (Display.getHeight() - terminalHeight) / 2 + terminalHeight;
					heldWindow.width = Display.getWidth() / 2;
					heldWindow.height = (Display.getHeight() - terminalHeight) /2;
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = 0;
					heldWindow.resizeY = Display.getHeight();
					heldWindow.resizeWidth = Display.getWidth() / 2;
					heldWindow.resizeHeight = (Display.getHeight() - terminalHeight) /2;
				}
			}
			//Lower left corner
			else if (Mouse.getY() < 10) {
				if (released) {
					heldWindow.x = 0;
					heldWindow.y = terminalHeight;
					heldWindow.width = Display.getWidth() / 2;
					heldWindow.height = (Display.getHeight() - terminalHeight) /2;
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = 0;
					heldWindow.resizeY = (Display.getHeight() - terminalHeight) /2 + terminalHeight;
					heldWindow.resizeWidth = Display.getWidth() / 2;
					heldWindow.resizeHeight = (Display.getHeight() - terminalHeight) /2;
				}
			}
			//Just Left Side
			else {
				if (released) {
					heldWindow.x = 0;
					heldWindow.y = terminalHeight;
					heldWindow.width = Display.getWidth()/2;
					heldWindow.height = Display.getHeight() - terminalHeight;
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = 0;
					heldWindow.resizeY = Display.getHeight();
					heldWindow.resizeWidth = Display.getWidth()/2;
					heldWindow.resizeHeight = Display.getHeight() - terminalHeight;
				}
			}
		}
		//Right Side
		else if (Mouse.getX() > Display.getWidth() - 10) {
			//Upper corner
			if (Mouse.getY() > Display.getHeight() - 10) {
				if (released) {
					heldWindow.x = Display.getWidth() /2;
					heldWindow.y = (Display.getHeight() - terminalHeight) / 2 + terminalHeight;
					heldWindow.width = Display.getWidth() /2;
					heldWindow.height = (Display.getHeight() - terminalHeight) / 2;
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = Display.getWidth() / 2;
					heldWindow.resizeY = Display.getHeight();
					heldWindow.resizeWidth = Display.getWidth() / 2;
					heldWindow.resizeHeight = (Display.getHeight() - terminalHeight) / 2;
				}
			}
			//Lower corner
			else if (Mouse.getY() < 10) {
				if (released) {
					heldWindow.x = Display.getWidth() / 2;
					heldWindow.y = terminalHeight;
					heldWindow.width = Display.getWidth() / 2;
					heldWindow.height = (Display.getHeight() - terminalHeight) / 2;
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = Display.getWidth() / 2;
					heldWindow.resizeY = (Display.getHeight() - terminalHeight) / 2 + terminalHeight;
					heldWindow.resizeWidth = Display.getWidth() / 2;
					heldWindow.resizeHeight = (Display.getHeight() - terminalHeight) / 2;
				}
			}
			//Just right side
			else {
				if (released) {
					heldWindow.x = Display.getWidth()/2;
					heldWindow.y = terminalHeight;
					heldWindow.width = Display.getWidth()/2;
					heldWindow.height = Display.getHeight() - terminalHeight;
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = Display.getWidth() / 2;
					heldWindow.resizeY = Display.getHeight();
					heldWindow.resizeWidth = Display.getWidth()/2;
					heldWindow.resizeHeight = Display.getHeight() - terminalHeight;
				}
			}
		}
		//Bottom Side
		else if (Mouse.getY() < 10) {
			if (released) {
				heldWindow.x = 0;
				heldWindow.y = terminalHeight;
				heldWindow.width = Display.getWidth();
				heldWindow.height = (Display.getHeight() - terminalHeight) / 2;
				;
			}
			else {
				heldWindow.resizing = true;
				heldWindow.resizeX = 0;
				heldWindow.resizeY = (Display.getHeight() - terminalHeight) / 2 + terminalHeight;
				heldWindow.resizeWidth = Display.getWidth();
				heldWindow.resizeHeight = (Display.getHeight() - terminalHeight) / 2;
			}
		}
		//Top side
		else if (Mouse.getY() > Display.getHeight() - 10) {
			if (released) {
				heldWindow.x = 0;
				heldWindow.y = (Display.getHeight() - terminalHeight) / 2 + terminalHeight;
				heldWindow.width = Display.getWidth();
				heldWindow.height = (Display.getHeight() - terminalHeight) / 2;
				;
			}
			else{
				heldWindow.resizing = true;
				heldWindow.resizeX = 0;
				heldWindow.resizeY = Display.getHeight();
				heldWindow.resizeWidth = Display.getWidth();
				heldWindow.resizeHeight = (Display.getHeight() - terminalHeight) / 2;
			}
		}
		else heldWindow.resizing = false;
	}
	
	public static void main(String[] argv) {
		Param0 display = new Param0();
		display.start();
	}
}