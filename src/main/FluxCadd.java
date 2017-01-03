package main;

import event.EventListener;
import event.EventMessage;
import graphics.FluxCaddWindow;
import graphics.Window_LWJGL;
import input.Keyboard;
import input.KeyboardEvent;
import input.MouseButton;
import input.MouseButtonEvent;
import input.MouseCursor;
import input.MouseCursorEvent;
import input.MouseWheel;
import input.MouseWheelEvent;

import java.util.Iterator;

import lisp.Content_Lisp;
import robocam.Content_Cam;
import ui.Content_Terminal;
import ui.ViewType;
import ui.Panel;
import ui.Content_View;
import ui.PanelManager;
import static org.lwjgl.opengl.GL11.*;

public class FluxCadd implements EventListener {
	
	public static PanelManager panelManager;
	
	public static Panel terminal;
	
	public Panel heldWindow = null;
	public Panel resizingWindow = null;
	public Panel draggedWindow = null;
	
	public int lastPressX = -1;
	public int lastPressY = -1;
	
	public int terminalHeight = 60;
	
	public static FluxCaddWindow window;
	
	public static void main(String[] argv) {
		FluxCadd display = new FluxCadd();
		display.start();
	}
	
	public void start() {
		
		window = new Window_LWJGL();
		window.init();
		
		panelManager = new PanelManager();
		
		terminal = new Panel("terminal");
		panelManager.add(terminal);
		
		Keyboard.instance().register(this);
		MouseButton.instance().register(this);
		MouseCursor.instance().register(this);
		MouseWheel.instance().register(this);
		
		initCAMWindows();
		
		glClearColor(0.4f,0.4f,1,1);
		
		window.loop();
	}
	
	public void initCAMWindows() {
		int w = window.getWidth();
		int h = window.getHeight();
		
		Panel previewWindow = new Panel(0,terminal.getHeight(),w/2,h - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		panelManager.add(previewWindow);
		previewWindow.closeable = false;
		previewWindow.resizable = false;
		previewWindow.moveable = false;

		Panel camWindow = new Panel(w/2,terminal.getHeight(),w/2,h - terminal.getHeight());
		camWindow.content = new Content_Cam(camWindow,(Content_View)previewWindow.content);
		panelManager.add(camWindow);
		camWindow.closeable = false;
		camWindow.resizable = false;
		camWindow.moveable = false;
	}
	
	public void initCADWindows() {
		int w = window.getWidth();
		int h = window.getHeight();
		
		Panel previewWindow = new Panel(0,terminal.getHeight(),w/2,h - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		panelManager.add(previewWindow);
		
		Panel codeWindow = new Panel(w/2,terminal.getHeight(),w/2,h - terminal.getHeight());
		codeWindow.content = new Content_Lisp(codeWindow,(Content_View)previewWindow.content);
		panelManager.add(codeWindow);
	}
	
	private void mousePressed(int button, int x, int y) {
		Iterator<Panel> itr = panelManager.getIterator();
		while(itr.hasNext()) {
			Panel p = itr.next();
			
			if (p.pick(x,y)) {
				if (p.pickBar(x,y)) heldWindow = p;
				else if (p.pickResize(x,y)) resizingWindow = p;
				else draggedWindow = p;
				itr.remove();
				panelManager.addTop(p);
				p.mousePressed(button,x,y);
				break;
			}
		}
	}
	
	private void mouseReleased(int button, int x, int y) {
		if (button != 0) return;
		
		if (heldWindow != null) {
			checkEdges(true,x,y);
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
	
	private void mouseDragged(int x, int y,int dx, int dy) {	
		if (heldWindow != null) {
			checkEdges(false, x, y);
			heldWindow.move(dx,dy);
		}
		else if (resizingWindow != null) {
			int newX = x - resizingWindow.getX();
			int newY = (resizingWindow.getY() + resizingWindow.getHeight()) - y;
			
			resizingWindow.startResize(newX,newY);
		}
		else if (draggedWindow != null) {
			draggedWindow.mouseDragged(dx, dy);
		}
	}
	
	private void mouseClicked(int x, int y) {
		Iterator<Panel> itr = panelManager.getIterator();
		while(itr.hasNext()) {
			Panel w = itr.next();
			if (w.pickClose(x,y)) {
				itr.remove();
			}
		}
	}
	
	private void mouseWheel(int dx, int dy) {
		panelManager.panels.get(0).content.mouseWheel(dy/12);
	}
	
	public void checkEdges(boolean released, int x , int y) {
		//Left Side
		if (x < 10) {
			//Upper Left Corner
			if (y > window.getHeight() - 10) {
				if (released) {
					heldWindow.setX(0);
					heldWindow.setY((window.getHeight() - terminalHeight) / 2 + terminalHeight);
					heldWindow.setWidth(window.getWidth() / 2);
					heldWindow.setHeight((window.getHeight() - terminalHeight) /2);
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = 0;
					heldWindow.resizeY = window.getHeight();
					heldWindow.resizeWidth = window.getWidth() / 2;
					heldWindow.resizeHeight = (window.getHeight() - terminalHeight) /2;
				}
			}
			//Lower left corner
			else if (y < 10) {
				if (released) {
					heldWindow.setX(0);
					heldWindow.setY(terminalHeight);
					heldWindow.setWidth(window.getWidth() / 2);
					heldWindow.setHeight((window.getHeight() - terminalHeight) /2);
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = 0;
					heldWindow.resizeY = (window.getHeight() - terminalHeight) /2 + terminalHeight;
					heldWindow.resizeWidth = window.getWidth() / 2;
					heldWindow.resizeHeight = (window.getHeight() - terminalHeight) /2;
				}
			}
			//Just Left Side
			else {
				if (released) {
					heldWindow.setX(0);
					heldWindow.setY(terminalHeight);
					heldWindow.setWidth(window.getWidth()/2);
					heldWindow.setHeight(window.getHeight() - terminalHeight);
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = 0;
					heldWindow.resizeY = window.getHeight();
					heldWindow.resizeWidth = window.getWidth()/2;
					heldWindow.resizeHeight = window.getHeight() - terminalHeight;
				}
			}
		}
		//Right Side
		else if (x > window.getWidth() - 10) {
			//Upper corner
			if (y > window.getHeight() - 10) {
				if (released) {
					heldWindow.setX(window.getWidth() / 2);
					heldWindow.setY((window.getHeight() - terminalHeight) / 2 + terminalHeight);
					heldWindow.setWidth(window.getWidth() / 2);
					heldWindow.setHeight((window.getHeight() - terminalHeight) / 2);
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = window.getWidth() / 2;
					heldWindow.resizeY = window.getHeight();
					heldWindow.resizeWidth = window.getWidth() / 2;
					heldWindow.resizeHeight = (window.getHeight() - terminalHeight) / 2;
				}
			}
			//Lower corner
			else if (y < 10) {
				if (released) {
					heldWindow.setX(window.getWidth() / 2);
					heldWindow.setY(terminalHeight);
					heldWindow.setWidth(window.getWidth() / 2);
					heldWindow.setHeight((window.getHeight() - terminalHeight) / 2);
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = window.getWidth() / 2;
					heldWindow.resizeY = (window.getHeight() - terminalHeight) / 2 + terminalHeight;
					heldWindow.resizeWidth = window.getWidth() / 2;
					heldWindow.resizeHeight = (window.getHeight() - terminalHeight) / 2;
				}
			}
			//Just right side
			else {
				if (released) {
					heldWindow.setX(window.getWidth()/2);
					heldWindow.setY(terminalHeight);
					heldWindow.setWidth(window.getWidth()/2);
					heldWindow.setHeight(window.getHeight() - terminalHeight);
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = window.getWidth() / 2;
					heldWindow.resizeY = window.getHeight();
					heldWindow.resizeWidth = window.getWidth()/2;
					heldWindow.resizeHeight = window.getHeight() - terminalHeight;
				}
			}
		}
		//Bottom Side
		else if (y < 10) {
			if (released) {
				heldWindow.setX(0);
				heldWindow.setY(terminalHeight);
				heldWindow.setWidth(window.getWidth());
				heldWindow.setHeight((window.getHeight() - terminalHeight) / 2);
			}
			else {
				heldWindow.resizing = true;
				heldWindow.resizeX = 0;
				heldWindow.resizeY = (window.getHeight() - terminalHeight) / 2 + terminalHeight;
				heldWindow.resizeWidth = window.getWidth();
				heldWindow.resizeHeight = (window.getHeight() - terminalHeight) / 2;
			}
		}
		//Top side
		else if (y > window.getHeight() - 10) {
			if (released) {
				heldWindow.setX(0);
				heldWindow.setY((window.getHeight() - terminalHeight) / 2 + terminalHeight);
				heldWindow.setWidth(window.getWidth());
				heldWindow.setHeight((window.getHeight() - terminalHeight) / 2);
			}
			else{
				heldWindow.resizing = true;
				heldWindow.resizeX = 0;
				heldWindow.resizeY = window.getHeight();
				heldWindow.resizeWidth = window.getWidth();
				heldWindow.resizeHeight = (window.getHeight() - terminalHeight) / 2;
			}
		}
		else heldWindow.resizing = false;
	}
	

	
	public static void printToTerminal(String s) {
		((Content_Terminal)terminal.content).addString(s);
	}
	
	@Override
	public void message(EventMessage message) {
		if (message instanceof KeyboardEvent) {
			KeyboardEvent event = (KeyboardEvent)message;
			if (event.type == KeyboardEvent.Type.PRESSED) {
				panelManager.panels.get(0).content.keyPressed(event.key);
			}
		}
		else if (message instanceof MouseButtonEvent) {
			MouseButtonEvent event = (MouseButtonEvent) message;
			if (event.type == MouseButtonEvent.Type.PRESSED) {
				mousePressed(event.button,MouseCursor.instance().getX(),MouseCursor.instance().getY());
			}
			else {
				mouseReleased(event.button,MouseCursor.instance().getX(),MouseCursor.instance().getY());
			}
		}
		else if (message instanceof MouseCursorEvent) {
			MouseCursorEvent event = (MouseCursorEvent) message;
			if (MouseButton.instance().leftPressed()) {
				mouseDragged((int)event.x,(int)event.y,MouseCursor.instance().getDX(),MouseCursor.instance().getDY());
			}
		}
		else if (message instanceof MouseWheelEvent) {
			MouseWheelEvent event = (MouseWheelEvent) message;
			mouseWheel(0,event.dy);
		}
		
	}
}