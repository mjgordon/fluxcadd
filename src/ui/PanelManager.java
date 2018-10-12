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
import backend.Backend;
import lisp.Content_Lisp;
import main.FluxCadd;
import event.*;
import static org.lwjgl.glfw.GLFW.*;

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

		terminal = new Panel("terminal");
		addPanel(terminal);
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
		return(panels.get(0));
	}

	@Override
	public void message(EventMessage message) {
		if (message instanceof KeyboardEvent) {
			KeyboardEvent event = (KeyboardEvent) message;
			if (event.type == GLFW_PRESS) {
				getTopPanel().content.keyPressed(event.key);
			}
		} else if (message instanceof TextInputEvent) {
			TextInputEvent event = (TextInputEvent) message;
			getTopPanel().content.textInput(event.character);
		} else if (message instanceof MouseButtonEvent) {
			MouseButtonEvent event = (MouseButtonEvent) message;
			if (event.type == MouseButtonEvent.Type.PRESSED) {
				mousePressed(event.button, MouseCursor.instance().getX(), MouseCursor.instance().getY());
			} else {
				mouseReleased(event.button, MouseCursor.instance().getX(), MouseCursor.instance().getY());
			}
		} else if (message instanceof MouseCursorEvent) {
			MouseCursorEvent event = (MouseCursorEvent) message;
			if (MouseButton.instance().anyPressed()) {
				mouseDragged((int) event.x, (int) event.y, MouseCursor.instance().getDX(), MouseCursor.instance().getDY());
			}
		} else if (message instanceof MouseWheelEvent) {
			MouseWheelEvent event = (MouseWheelEvent) message;
			mouseWheel(0, event.dy);
		}

	}

	private void mousePressed(int button, int x, int y) {
		Iterator<Panel> itr = panels.iterator();
		while (itr.hasNext()) {
			Panel p = itr.next();

			if (p.pick(x, y)) {
				if (p.pickBar(x, y))
					heldPanel = p;
				else if (p.pickResize(x, y))
					resizingPanel = p;
				else
					draggedPanel = p;
				itr.remove();
				addPanelTop(p);
				p.mousePressed(button, x, y);
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
		} else if (resizingPanel != null) {
			int newX = x - resizingPanel.getX();
			int newY = (resizingPanel.getY() + resizingPanel.getHeight()) - y;

			resizingPanel.startResize(newX, newY);
		} else if (draggedPanel != null) {
			draggedPanel.mouseDragged(dx, dy);
		}
	}

	private void mouseClicked(int x, int y) {
		Iterator<Panel> itr = panels.iterator();
		while (itr.hasNext()) {
			Panel w = itr.next();
			if (w.pickClose(x, y)) {
				itr.remove();
			}
		}
	}

	private void mouseWheel(int dx, int dy) {
		getTopPanel().content.mouseWheel(dy);
	}

	public void checkEdges(boolean released, int x, int y) {
		// TODO: STATIC HERESY
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
				} else {
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
				} else {
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
				} else {
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
				} else {
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
				} else {
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
				} else {
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
			} else {
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
			} else {
				heldPanel.resizing = true;
				heldPanel.resizeX = 0;
				heldPanel.resizeY = backend.getHeight();
				heldPanel.resizeWidth = backend.getWidth();
				heldPanel.resizeHeight = (backend.getHeight() - terminal.getHeight()) / 2;
			}
		} else
			heldPanel.resizing = false;
	}

	public void initCAMWindows() {
		Backend backend = FluxCadd.backend;
		int w = backend.getWidth();
		int h = backend.getHeight();

		Panel previewWindow = new Panel(0, terminal.getHeight(), w / 2, h - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		addPanel(previewWindow);
		previewWindow.closeable = false;
		previewWindow.resizable = false;
		previewWindow.moveable = false;

		Panel camWindow = new Panel(w / 2, terminal.getHeight(), w / 2, h - terminal.getHeight());
		camWindow.content = new Content_Cam(camWindow, (Content_View) previewWindow.content);
		addPanel(camWindow);
		camWindow.closeable = false;
		camWindow.resizable = false;
		camWindow.moveable = false;
	}

	public void initCADWindows() {
		Backend backend = FluxCadd.backend;
		int w = backend.getWidth();
		int h = backend.getHeight();

		Panel previewWindow = new Panel(0, terminal.getHeight(), w / 2, h - terminal.getHeight());
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		addPanel(previewWindow);

		Panel codeWindow = new Panel(w / 2, terminal.getHeight(), w / 2, h - terminal.getHeight());
		codeWindow.content = new Content_Lisp(codeWindow, (Content_View) previewWindow.content);
		addPanel(codeWindow);
	}

}
