package ui;

import io.Keyboard;
import io.KeyboardEvent;
import io.MouseButton;
import io.MouseButtonEvent;
import io.MouseCursor;
import io.MouseCursorEvent;
import io.MouseWheel;
import io.MouseWheelEvent;
import io.TextInput;
import io.TextInputEvent;

import java.util.ArrayList;
import java.util.Iterator;

import robocam.Content_Cam;
import scheme.Content_Scheme;
import backend.Backend;
import main.FluxCadd;
import mattersite.Content_Mattersite;
import render_sdf.renderer.Content_Renderer;
import event.*;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Primary UI Manager. Stores a list of panels (sub-windows) and is responsible
 * for sending user input to them
 *
 */
public class PanelManager implements EventListener {
	private ArrayList<Panel> panels;

	private Panel terminal;

	private Panel heldPanel = null;
	private Panel resizingPanel = null;
	private Panel draggedPanel = null;

	public PanelManager() {
		panels = new ArrayList<Panel>();

		Keyboard.instance().register(this);
		TextInput.instance().register(this);
		MouseButton.instance().register(this);
		MouseCursor.instance().register(this);
		MouseWheel.instance().register(this);

		resetPanels();
	}

	public void render() {
		for (int i = 0; i < panels.size(); i++) {
			int id = panels.size() - i - 1;
			panels.get(id).render(id == 0);
		}
	}

	public void addPanel(Panel w) {
		panels.add(w);
	}

	public void addPanelTop(Panel w) {
		panels.add(0, w);
	}

	public Panel getTopPanel() {
		return (panels.get(0));
	}

	@Override
	public void message(EventMessage message) {
		//TODO make this a switch statement
		if (message instanceof KeyboardEvent) {
			KeyboardEvent event = (KeyboardEvent) message;
			if (event.type == GLFW_PRESS) {
				getTopPanel().content.keyPressed(event.key);
			}
		}
		else if (message instanceof TextInputEvent) {
			TextInputEvent event = (TextInputEvent) message;
			getTopPanel().content.textInput(event.character);
		}
		else if (message instanceof MouseButtonEvent) {
			MouseButtonEvent event = (MouseButtonEvent) message;
			if (event.type == MouseButtonEvent.Type.PRESSED) {
				mousePressed(event.button, MouseCursor.instance().getX(), MouseCursor.instance().getY());
			}
			else {
				mouseReleased(event.button, MouseCursor.instance().getX(), MouseCursor.instance().getY());
			}
		}
		else if (message instanceof MouseCursorEvent) {
			MouseCursorEvent event = (MouseCursorEvent) message;
			if (MouseButton.instance().anyPressed()) {
				mouseDragged((int) event.x, (int) event.y, MouseCursor.instance().getDX(),
						MouseCursor.instance().getDY());
			}
		}
		else if (message instanceof MouseWheelEvent) {
			MouseWheelEvent event = (MouseWheelEvent) message;
			mouseWheel(0, event.dy);
		}
	}

	private void mousePressed(int button, int x, int y) {
		Iterator<Panel> itr = panels.iterator();
		while (itr.hasNext()) {
			Panel panel = itr.next();

			if (panel.pick(x, y)) {
				if (panel.pickBar(x, y))
					heldPanel = panel;
				else if (panel.pickResize(x, y))
					resizingPanel = panel;
				else
					draggedPanel = panel;
				itr.remove();
				addPanelTop(panel);
				panel.mousePressed(button, x, y);
				break;
			}
		}
	}

	private void mouseReleased(int button, int x, int y) {
		if (button != 0)
			return;

		if (heldPanel != null) {
			checkEdges(true, x, y);
			heldPanel.resizing = false;
			heldPanel = null;
		}
		if (resizingPanel != null) {
			resizingPanel.endResize();
			resizingPanel = null;
		}

		if (draggedPanel != null) {
			draggedPanel = null;
		}
	}

	private void mouseDragged(int x, int y, int dx, int dy) {
		if (heldPanel != null) {
			checkEdges(false, x, y);
			heldPanel.move(dx, dy);
		}
		else if (resizingPanel != null) {
			int newX = x - resizingPanel.getX();
			int newY = (resizingPanel.getY() + resizingPanel.getHeight()) - y;

			resizingPanel.startResize(newX, newY);
		}
		else if (draggedPanel != null) {
			draggedPanel.mouseDragged(dx, dy);
		}
	}

	// TODO : FEATURE : Implement mouseClicked(). Necessary?
	/*
	 * private void mouseClicked(int x, int y) { Iterator<Panel> itr =
	 * panels.iterator(); while (itr.hasNext()) { Panel w = itr.next(); if
	 * (w.pickClose(x, y)) { itr.remove(); } } }
	 */

	private void mouseWheel(int dx, int dy) {
		getTopPanel().content.mouseWheel(dy);
	}

	private void checkEdges(boolean released, int x, int y) {
		//TODO: get width and height first here
		Backend backend = FluxCadd.backend;

		// Left Side
		if (x < 10) {
			// Upper Left Corner
			if (y > backend.getHeight() - 10) {
				if (released) {
					heldPanel.setX(0);
					heldPanel.setY((backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight());
					heldPanel.setWidth(backend.getWidth() / 2);
					heldPanel.setHeight((backend.getHeight() - terminal.getHeight()) / 2);
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = 0;
					heldPanel.resizeY = backend.getHeight();
					heldPanel.resizeWidth = backend.getWidth() / 2;
					heldPanel.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
				}
			}
			// Lower left corner
			else if (y < 10) {
				if (released) {
					heldPanel.setX(0);
					heldPanel.setY(terminal.getHeight());
					heldPanel.setWidth(backend.getWidth() / 2);
					heldPanel.setHeight((backend.getHeight() - terminal.getHeight()) / 2);
					;
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = 0;
					heldPanel.resizeY = (backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight();
					heldPanel.resizeWidth = backend.getWidth() / 2;
					heldPanel.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
				}
			}
			// Just Left Side
			else {
				if (released) {
					heldPanel.setX(0);
					heldPanel.setY(terminal.getHeight());
					heldPanel.setWidth(backend.getWidth() / 2);
					heldPanel.setHeight(backend.getHeight() - terminal.getHeight());
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = 0;
					heldPanel.resizeY = backend.getHeight();
					heldPanel.resizeWidth = backend.getWidth() / 2;
					heldPanel.resizeHeight = backend.getHeight() - terminal.getHeight();
				}
			}
		}
		// Right Side
		else if (x > backend.getWidth() - 10) {
			// Upper corner
			if (y > backend.getHeight() - 10) {
				if (released) {
					heldPanel.setX(backend.getWidth() / 2);
					heldPanel.setY((backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight());
					heldPanel.setWidth(backend.getWidth() / 2);
					heldPanel.setHeight((backend.getHeight() - terminal.getHeight()) / 2);
					;
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = backend.getWidth() / 2;
					heldPanel.resizeY = backend.getHeight();
					heldPanel.resizeWidth = backend.getWidth() / 2;
					heldPanel.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
				}
			}
			// Lower corner
			else if (y < 10) {
				if (released) {
					heldPanel.setX(backend.getWidth() / 2);
					heldPanel.setY(terminal.getHeight());
					heldPanel.setWidth(backend.getWidth() / 2);
					heldPanel.setHeight((backend.getHeight() - terminal.getHeight()) / 2);
					;
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = backend.getWidth() / 2;
					heldPanel.resizeY = (backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight();
					heldPanel.resizeWidth = backend.getWidth() / 2;
					heldPanel.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
				}
			}
			// Just right side
			else {
				if (released) {
					heldPanel.setX(backend.getWidth() / 2);
					heldPanel.setY(terminal.getHeight());
					heldPanel.setWidth(backend.getWidth() / 2);
					heldPanel.setHeight(backend.getHeight() - terminal.getHeight());
					;
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = backend.getWidth() / 2;
					heldPanel.resizeY = backend.getHeight();
					heldPanel.resizeWidth = backend.getWidth() / 2;
					heldPanel.resizeHeight = backend.getHeight() - terminal.getHeight();
				}
			}
		}
		// Bottom Side
		else if (y < 10) {
			if (released) {
				heldPanel.setX(0);
				heldPanel.setY(terminal.getHeight());
				heldPanel.setWidth(backend.getWidth());
				heldPanel.setHeight((backend.getHeight() - terminal.getHeight()) / 2);
			}
			else {
				heldPanel.resizing = true;
				heldPanel.resizeX = 0;
				heldPanel.resizeY = (backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight();
				heldPanel.resizeWidth = backend.getWidth();
				heldPanel.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
			}
		}
		// Top side
		else if (y > backend.getHeight() - 10) {
			if (released) {
				heldPanel.setX(0);
				heldPanel.setY((backend.getHeight() - terminal.getHeight()) / 2 + terminal.getHeight());
				heldPanel.setWidth(backend.getWidth());
				heldPanel.setHeight((backend.getHeight() - terminal.getHeight()) / 2);
			}
			else {
				heldPanel.resizing = true;
				heldPanel.resizeX = 0;
				heldPanel.resizeY = backend.getHeight();
				heldPanel.resizeWidth = backend.getWidth();
				heldPanel.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
			}
		}
		else
			heldPanel.resizing = false;
	}

	/**
	 * Clears all existing panels and recreates the Terminal Panel
	 */
	public void resetPanels() {
		panels.clear();
		terminal = new Panel("terminal");
		addPanel(terminal);
	}
	
	/**
	 * Resize existing panels due to a window resizing event
	 * @param w
	 * @param h
	 */
	public void resizePanels(int w, int h) {
		//TODO: Make this not hardcoded
		Panel left = panels.get(1);
		Panel right = panels.get(2);
		
		left.setX(0);
		right.setX(w / 2);
		
		left.setWidth(w / 2);
		right.setWidth(w / 2);
		
		left.setHeight(h - terminal.getHeight());
		right.setHeight(h - terminal.getHeight());
		
		System.out.println("New Window Size : " + w + "," + h);
	}

	/**
	 * Setup panels for using RoboCam or Drawbot functionality
	 */
	public void initCAMWindows() {
		int w = FluxCadd.backend.getWidth();
		int h = FluxCadd.backend.getHeight();

		Panel previewWindow = new Panel(0, terminal.getHeight(), w / 2, h - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		previewWindow.closeable = false;
		previewWindow.resizable = false;
		previewWindow.moveable = false;
		addPanel(previewWindow);

		Panel camWindow = new Panel(w / 2, terminal.getHeight(), w / 2, h - terminal.getHeight());
		camWindow.content = new Content_Cam(camWindow, (Content_View) previewWindow.content);
		camWindow.closeable = false;
		camWindow.resizable = false;
		camWindow.moveable = false;
		addPanel(camWindow);
	}

	/**
	 * Setup panels for using scheme functionality
	 */
	public void initCADWindows() {
		int w = FluxCadd.backend.getWidth();
		int h = FluxCadd.backend.getHeight();

		Panel previewWindow = new Panel(0, terminal.getHeight(), w / 2, h - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		addPanel(previewWindow);

		Panel codeWindow = new Panel(w / 2, terminal.getHeight(), w / 2, h - terminal.getHeight());
		codeWindow.content = new Content_Scheme(codeWindow, (Content_View) previewWindow.content);
		addPanel(codeWindow);
	}
	
	public void initMattersiteWindows() {
		int w = FluxCadd.backend.getWidth();
		int h = FluxCadd.backend.getHeight();

		Panel previewWindow = new Panel(0, terminal.getHeight(), w / 2, h - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		previewWindow.closeable = false;
		previewWindow.resizable = false;
		previewWindow.moveable = false;
		addPanel(previewWindow);

		Panel camWindow = new Panel(w / 2, terminal.getHeight(), w / 2, h - terminal.getHeight());
		camWindow.content = new Content_Mattersite(camWindow,(Content_View)previewWindow.content);
		camWindow.closeable = false;
		camWindow.resizable = false;
		camWindow.moveable = false;
		addPanel(camWindow);
	}
	
	public void initSDFWindows() {
		int w = FluxCadd.backend.getWidth();
		int h = FluxCadd.backend.getHeight();
		
		int split = w / 3 * 2;

		Panel previewWindow = new Panel(0, terminal.getHeight(), split, h - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		previewWindow.closeable = false;
		previewWindow.moveable = false;
		previewWindow.resizable = false;
		addPanel(previewWindow);

		Panel controlWindow = new Panel(split, terminal.getHeight(), w - split, h - terminal.getHeight());
		controlWindow.content = new Content_Renderer(controlWindow, (Content_View) previewWindow.content);
		controlWindow.closeable = false;
		controlWindow.moveable = false;
		controlWindow.resizable = false;
		addPanel(controlWindow);
	}

	/**
	 * Setup panels to choose desired workspace
	 */
	public void initChooser() {
		int w = FluxCadd.backend.getWidth();
		int h = FluxCadd.backend.getHeight();

		Panel chooserWindow = new Panel(0, terminal.getHeight(), w, h - terminal.getHeight());
		chooserWindow.content = new Content_Chooser(chooserWindow);
		chooserWindow.windowTitle = "Workspace Chooser";
		addPanel(chooserWindow);

	}

}
