package main;

import java.util.Iterator;

import lisp.Content_Lisp;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import robocam.Content_Cam;

import ui.Content_Terminal;
import ui.ViewType;
import ui.Window;
import ui.Content_View;
import ui.WindowManager;
import static org.lwjgl.opengl.GL11.*;

public class Param0 {
	
	public WindowManager windowManager;
	
	public static Window terminal;
	
	public Window heldWindow = null;
	public Window resizingWindow = null;
	public Window draggedWindow = null;
	
	public int lastPressX = -1;
	public int lastPressY = -1;
	
	public int terminalHeight = 60;
	
		
	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(1024,768));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		Display.setTitle("Robocam");
		
		windowManager = new WindowManager();
		
		terminal = new Window("terminal");
		windowManager.add(terminal);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 800, 0, 600, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		
		
		//Setup robot windows
		Window previewWindow = new Window(0,terminal.getHeight(),Display.getWidth()/2,Display.getHeight() - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		windowManager.add(previewWindow);
//		previewWindow.closeable = false;
//		previewWindow.resizable = false;
//		previewWindow.moveable = false;
//		
		Window camWindow = new Window(Display.getWidth()/2,terminal.getHeight(),Display.getWidth()/2,Display.getHeight() - terminal.getHeight());
		camWindow.content = new Content_Cam(camWindow,(Content_View)previewWindow.content);
		windowManager.add(camWindow);
//		camWindow.closeable = false;
//		camWindow.resizable = false;
//		camWindow.moveable = false;
		//End setup robot windows
		
		//Setup Lisp Windows
//		Window previewWindow = new Window(0,terminal.getHeight(),Display.getWidth()/2,Display.getHeight() - terminal.getHeight());
//		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
//		windowManager.add(previewWindow);
//		
//		Window codeWindow = new Window(Display.getWidth()/2,terminal.getHeight(),Display.getWidth()/2,Display.getHeight() - terminal.getHeight());
//		codeWindow.content = new Content_Lisp();
//		windowManager.add(codeWindow);

		//End Setup Lisp Windows
		
		glClearColor(0.4f,0.4f,1,1);
		
		

		
		
		//Main program loop
		while (!Display.isCloseRequested()) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); 
			
			
			while(Mouse.next()) parseMouse();
			while(Keyboard.next()) parseKeyboard();
			windowManager.render();
			Display.update();
			Display.sync(30);
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
		
		Iterator<Window> itr = windowManager.getIterator();
		while(itr.hasNext()) {
			Window w = itr.next();
			
			if (w.pick()) {
				if (w.pickBar()) heldWindow = w;
				else if (w.pickResize()) resizingWindow = w;
				else draggedWindow = w;
				itr.remove();
				windowManager.addTop(w);
				w.mousePressed();
				break;
			}
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
			int newX = Mouse.getX() - resizingWindow.getX();
			int newY = (resizingWindow.getY() + resizingWindow.getHeight()) - Mouse.getY();
			
			resizingWindow.startResize(newX,newY);
		}
		else if (draggedWindow != null) {
			draggedWindow.mouseDragged();
		}
	}
	
	private void mouseClicked() {
		Iterator<Window> itr = windowManager.getIterator();
		while(itr.hasNext()) {
			Window w = itr.next();
			if (w.pickClose()) {
				itr.remove();
			}
		}
	}
	
	private void mouseWheel() {
		windowManager.windows.get(0).content.mouseWheel(Mouse.getEventDWheel()/-12);
	}
	
	public void parseKeyboard() {
		if (Keyboard.getEventKeyState() == true) windowManager.windows.get(0).content.keyPressed();
	}
	
	public void checkEdges(boolean released) {
		//Left Side
		if (Mouse.getX() < 10) {
			//Upper Left Corner
			if (Mouse.getY() > Display.getHeight() - 10) {
				if (released) {
					heldWindow.setX(0);
					heldWindow.setY((Display.getHeight() - terminalHeight) / 2 + terminalHeight);
					heldWindow.setWidth(Display.getWidth() / 2);
					heldWindow.setHeight((Display.getHeight() - terminalHeight) /2);
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
					heldWindow.setX(0);
					heldWindow.setY(terminalHeight);
					heldWindow.setWidth(Display.getWidth() / 2);
					heldWindow.setHeight((Display.getHeight() - terminalHeight) /2);
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
					heldWindow.setX(0);
					heldWindow.setY(terminalHeight);
					heldWindow.setWidth(Display.getWidth()/2);
					heldWindow.setHeight(Display.getHeight() - terminalHeight);
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
					heldWindow.setX(Display.getWidth() / 2);
					heldWindow.setY((Display.getHeight() - terminalHeight) / 2 + terminalHeight);
					heldWindow.setWidth(Display.getWidth() / 2);
					heldWindow.setHeight((Display.getHeight() - terminalHeight) / 2);
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
					heldWindow.setX(Display.getWidth() / 2);
					heldWindow.setY(terminalHeight);
					heldWindow.setWidth(Display.getWidth() / 2);
					heldWindow.setHeight((Display.getHeight() - terminalHeight) / 2);
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
					heldWindow.setX(Display.getWidth()/2);
					heldWindow.setY(terminalHeight);
					heldWindow.setWidth(Display.getWidth()/2);
					heldWindow.setHeight(Display.getHeight() - terminalHeight);
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
				heldWindow.setX(0);
				heldWindow.setY(terminalHeight);
				heldWindow.setWidth(Display.getWidth());
				heldWindow.setHeight((Display.getHeight() - terminalHeight) / 2);
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
				heldWindow.setX(0);
				heldWindow.setY((Display.getHeight() - terminalHeight) / 2 + terminalHeight);
				heldWindow.setWidth(Display.getWidth());
				heldWindow.setHeight((Display.getHeight() - terminalHeight) / 2);
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
	
	public static void printToTerminal(String s) {
		((Content_Terminal)terminal.content).addString(s);
	}
}