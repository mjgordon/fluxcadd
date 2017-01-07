package ui;

import input.Keyboard;
import input.KeyboardEvent;
import input.MouseButton;
import input.MouseButtonEvent;
import input.MouseCursor;
import input.MouseCursorEvent;
import input.MouseWheel;
import input.MouseWheelEvent;
import input.TextInput;
import input.TextInputEvent;

import java.util.ArrayList;
import java.util.Iterator;

import robocam.Content_Cam;
import backend.Backend;
import lisp.Content_Lisp;
import main.FluxCadd;
import event.*;

import static org.lwjgl.glfw.GLFW.*;

public class PanelManager implements EventListener {
	public ArrayList<Panel> panels;
	
	public static Panel terminal;
	
	public Panel heldWindow = null;
	public Panel resizingWindow = null;
	public Panel draggedWindow = null;
	
	public PanelManager() {
		panels = new ArrayList<Panel>();
		
		Keyboard.instance().register(this);
		TextInput.instance().register(this);
		MouseButton.instance().register(this);
		MouseCursor.instance().register(this);
		MouseWheel.instance().register(this);
		
		terminal = new Panel("terminal");
		add(terminal);
	}
	
	public void render() {
		for (int i = 0; i < panels.size(); i++) {
			int id = panels.size() - i - 1;
			panels.get(id).render(id == 0);
		}
	}
	
	public void add(Panel w) {
		panels.add(w);
	}
	
	public void addTop(Panel w) {
		panels.add(0,w);
	}
	

	@Override
	public void message(EventMessage message) {
		if (message instanceof KeyboardEvent) {
			KeyboardEvent event = (KeyboardEvent)message;
			if (event.type == GLFW_PRESS) {
				panels.get(0).content.keyPressed(event.key);
			}
		}
		else if (message instanceof TextInputEvent) {
			TextInputEvent event = (TextInputEvent)message;
			panels.get(0).content.textInput(event.codepoint);
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
			if (MouseButton.instance().anyPressed()) {
				mouseDragged((int)event.x,(int)event.y,MouseCursor.instance().getDX(),MouseCursor.instance().getDY());
			}
		}
		else if (message instanceof MouseWheelEvent) {
			MouseWheelEvent event = (MouseWheelEvent) message;
			mouseWheel(0,event.dy);
		}
		
	}
	
	private void mousePressed(int button, int x, int y) {
		Iterator<Panel> itr = panels.iterator();
		while(itr.hasNext()) {
			Panel p = itr.next();
			
			if (p.pick(x,y)) {
				if (p.pickBar(x,y)) heldWindow = p;
				else if (p.pickResize(x,y)) resizingWindow = p;
				else draggedWindow = p;
				itr.remove();
				addTop(p);
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
		Iterator<Panel> itr = panels.iterator();
		while(itr.hasNext()) {
			Panel w = itr.next();
			if (w.pickClose(x,y)) {
				itr.remove();
			}
		}
	}
	
	private void mouseWheel(int dx, int dy) {
		panels.get(0).content.mouseWheel(dy);
	}
	
	public void checkEdges(boolean released, int x , int y) {
		//TODO: STATIC HERESY
		Backend backend = FluxCadd.backend;
		
		//Left Side
		if (x < 10) {
			//Upper Left Corner
			if (y > backend.getHeight() - 10) {
				if (released) {
					heldWindow.setX(0);
					heldWindow.setY((backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight());
					heldWindow.setWidth(backend.getWidth() / 2);
					heldWindow.setHeight((backend.getHeight() - terminal.getHeight()) /2);
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = 0;
					heldWindow.resizeY = backend.getHeight();
					heldWindow.resizeWidth = backend.getWidth() / 2;
					heldWindow.resizeHeight = (backend.getHeight() - terminal.getHeight()) /2;
				}
			}
			//Lower left corner
			else if (y < 10) {
				if (released) {
					heldWindow.setX(0);
					heldWindow.setY(terminal.getHeight());
					heldWindow.setWidth(backend.getWidth() / 2);
					heldWindow.setHeight((backend.getHeight() - terminal.getHeight()) /2);
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = 0;
					heldWindow.resizeY = (backend.getHeight() - terminal.getHeight()) /2 + terminal.getHeight();
					heldWindow.resizeWidth = backend.getWidth() / 2;
					heldWindow.resizeHeight = (backend.getHeight() - terminal.getHeight()) /2;
				}
			}
			//Just Left Side
			else {
				if (released) {
					heldWindow.setX(0);
					heldWindow.setY(terminal.getHeight());
					heldWindow.setWidth(backend.getWidth()/2);
					heldWindow.setHeight(backend.getHeight() - terminal.getHeight());
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = 0;
					heldWindow.resizeY = backend.getHeight();
					heldWindow.resizeWidth = backend.getWidth()/2;
					heldWindow.resizeHeight = backend.getHeight() - terminal.getHeight();
				}
			}
		}
		//Right Side
		else if (x > backend.getWidth() - 10) {
			//Upper corner
			if (y > backend.getHeight() - 10) {
				if (released) {
					heldWindow.setX(backend.getWidth() / 2);
					heldWindow.setY((backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight());
					heldWindow.setWidth(backend.getWidth() / 2);
					heldWindow.setHeight((backend.getHeight() - terminal.getHeight()) / 2);
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = backend.getWidth() / 2;
					heldWindow.resizeY = backend.getHeight();
					heldWindow.resizeWidth = backend.getWidth() / 2;
					heldWindow.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
				}
			}
			//Lower corner
			else if (y < 10) {
				if (released) {
					heldWindow.setX(backend.getWidth() / 2);
					heldWindow.setY(terminal.getHeight());
					heldWindow.setWidth(backend.getWidth() / 2);
					heldWindow.setHeight((backend.getHeight() - terminal.getHeight()) / 2);
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = backend.getWidth() / 2;
					heldWindow.resizeY = (backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight();
					heldWindow.resizeWidth = backend.getWidth() / 2;
					heldWindow.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
				}
			}
			//Just right side
			else {
				if (released) {
					heldWindow.setX(backend.getWidth()/2);
					heldWindow.setY(terminal.getHeight());
					heldWindow.setWidth(backend.getWidth()/2);
					heldWindow.setHeight(backend.getHeight() - terminal.getHeight());
					;
				}
				else {
					heldWindow.resizing = true;
					heldWindow.resizeX = backend.getWidth() / 2;
					heldWindow.resizeY = backend.getHeight();
					heldWindow.resizeWidth = backend.getWidth()/2;
					heldWindow.resizeHeight = backend.getHeight() - terminal.getHeight();
				}
			}
		}
		//Bottom Side
		else if (y < 10) {
			if (released) {
				heldWindow.setX(0);
				heldWindow.setY(terminal.getHeight());
				heldWindow.setWidth(backend.getWidth());
				heldWindow.setHeight((backend.getHeight() - terminal.getHeight()) / 2);
			}
			else {
				heldWindow.resizing = true;
				heldWindow.resizeX = 0;
				heldWindow.resizeY = (backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight();
				heldWindow.resizeWidth = backend.getWidth();
				heldWindow.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
			}
		}
		//Top side
		else if (y > backend.getHeight() - 10) {
			if (released) {
				heldWindow.setX(0);
				heldWindow.setY((backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight());
				heldWindow.setWidth(backend.getWidth());
				heldWindow.setHeight((backend.getHeight() - terminal.getHeight()) / 2);
			}
			else{
				heldWindow.resizing = true;
				heldWindow.resizeX = 0;
				heldWindow.resizeY = backend.getHeight();
				heldWindow.resizeWidth = backend.getWidth();
				heldWindow.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
			}
		}
		else heldWindow.resizing = false;
	}
	
	public void initCAMWindows() {
		Backend backend = FluxCadd.backend;
		int w = backend.getWidth();
		int h = backend.getHeight();
		
		Panel previewWindow = new Panel(0,terminal.getHeight(),w/2,h - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		add(previewWindow);
		previewWindow.closeable = false;
		previewWindow.resizable = false;
		previewWindow.moveable = false;

		Panel camWindow = new Panel(w/2,terminal.getHeight(),w/2,h - terminal.getHeight());
		camWindow.content = new Content_Cam(camWindow,(Content_View)previewWindow.content);
		add(camWindow);
		camWindow.closeable = false;
		camWindow.resizable = false;
		camWindow.moveable = false;
	}
	
	public void initCADWindows() {
		Backend backend = FluxCadd.backend;
		int w = backend.getWidth();
		int h = backend.getHeight();
		
		Panel previewWindow = new Panel(0,terminal.getHeight(),w/2,h - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		add(previewWindow);
		
		Panel codeWindow = new Panel(w/2,terminal.getHeight(),w/2,h - terminal.getHeight());
		codeWindow.content = new Content_Lisp(codeWindow,(Content_View)previewWindow.content);
		add(codeWindow);
	}

}
