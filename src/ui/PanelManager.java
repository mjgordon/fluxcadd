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

import org.lwjgl.opengl.GL11;

import scheme.Content_Scheme;
import main.FluxCadd;
import render_sdf.animation.Content_Animation;
import render_sdf.renderer.Content_Renderer;
import event.*;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Primary UI Manager. Stores a list of panels (sub-windows) and is responsible
 * for sending user input to them
 *
 */
public class PanelManager implements EventListener {
	private Panel head;

	private Panel terminal;

	private Panel heldPanel = null;
	private Panel resizingPanel = null;
	private Panel draggedPanel = null;
	
	private Panel activePanel = null;


	public PanelManager() {
		head = new Panel("terminal");

		Keyboard.instance().register(this);
		TextInput.instance().register(this);
		MouseButton.instance().register(this);
		MouseCursor.instance().register(this);
		MouseWheel.instance().register(this);

	}


	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, FluxCadd.backend.getHeight(), 0);
		GL11.glScalef(1,-1, 1);	
		
		head.render(activePanel);
		
		GL11.glPopMatrix();
	}



	@Override
	public void message(EventMessage message) {
		// TODO make this a switch statement (requires Java21)
		
		if (message instanceof KeyboardEvent) {
			KeyboardEvent event = (KeyboardEvent) message;
			if (event.type == GLFW_PRESS) {
				if (activePanel != null) {
					activePanel.content.keyPressed(event.key);
				}
			}
		}
		else if (message instanceof TextInputEvent) {
			TextInputEvent event = (TextInputEvent) message;
			if (activePanel != null) {
				activePanel.content.textInput(event.character);
			}
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
				int button = MouseButton.instance().getPressed();
				mouseDragged(button, (int) event.x, (int) event.y, MouseCursor.instance().getDX(), MouseCursor.instance().getDY());
			}
		}
		else if (message instanceof MouseWheelEvent) {
			MouseWheelEvent event = (MouseWheelEvent) message;
			mouseWheel(0, event.dy);
		}
	}


	private void mousePressed(int button, int x, int y) {		
		activePanel = head.pick(x, y);
		
		if (activePanel != null) {
			activePanel.mousePressed(button,x,y);
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
		
		if (activePanel != null) {
			activePanel.mouseReleased(button);
		}
	}


	private void mouseDragged(int button, int x, int y, int dx, int dy) {
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
			draggedPanel.mouseDragged(button, dx, dy);
		}
		
		if (activePanel != null) {
			activePanel.mouseDragged(button, dx, dy);
		}
	}


	private void mouseWheel(int dx, int dy) {
		Panel scrollPanel = head.pick(MouseCursor.instance().getX(), MouseCursor.instance().getY());
		if (scrollPanel != null) {
			scrollPanel.content.mouseWheel(dy);
		}
	}


	private void checkEdges(boolean released, int x, int y) {
		int wWidth = FluxCadd.backend.getWidth();
		int wHeight = FluxCadd.backend.getHeight();

		// Left Side
		if (x < 10) {
			// Upper Left Corner
			if (y > wHeight - 10) {
				if (released) {
					heldPanel.setX(0);
					heldPanel.setY((wHeight - terminal.getHeight()) / 2 + terminal.getHeight());
					heldPanel.setWidth(wWidth / 2);
					heldPanel.setHeight((wHeight - terminal.getHeight()) / 2);
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = 0;
					heldPanel.resizeY = wHeight;
					heldPanel.resizeWidth = wWidth / 2;
					heldPanel.resizeHeight = (wHeight - terminal.getHeight()) / 2;
				}
			}
			// Lower left corner
			else if (y < 10) {
				if (released) {
					heldPanel.setX(0);
					heldPanel.setY(terminal.getHeight());
					heldPanel.setWidth(wWidth / 2);
					heldPanel.setHeight((wHeight - terminal.getHeight()) / 2);
					;
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = 0;
					heldPanel.resizeY = (wHeight - terminal.getHeight()) / 2 + terminal.getHeight();
					heldPanel.resizeWidth = wWidth / 2;
					heldPanel.resizeHeight = (wHeight - terminal.getHeight()) / 2;
				}
			}
			// Just Left Side
			else {
				if (released) {
					heldPanel.setX(0);
					heldPanel.setY(terminal.getHeight());
					heldPanel.setWidth(wWidth / 2);
					heldPanel.setHeight(wHeight - terminal.getHeight());
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = 0;
					heldPanel.resizeY = wHeight;
					heldPanel.resizeWidth = wWidth / 2;
					heldPanel.resizeHeight = wHeight - terminal.getHeight();
				}
			}
		}
		// Right Side
		else if (x > wWidth - 10) {
			// Upper corner
			if (y > wHeight - 10) {
				if (released) {
					heldPanel.setX(wWidth / 2);
					heldPanel.setY((wHeight - terminal.getHeight()) / 2 + terminal.getHeight());
					heldPanel.setWidth(wWidth / 2);
					heldPanel.setHeight((wHeight - terminal.getHeight()) / 2);
					;
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = wWidth / 2;
					heldPanel.resizeY = wHeight;
					heldPanel.resizeWidth = wWidth / 2;
					heldPanel.resizeHeight = (wHeight - terminal.getHeight()) / 2;
				}
			}
			// Lower corner
			else if (y < 10) {
				if (released) {
					heldPanel.setX(wWidth / 2);
					heldPanel.setY(terminal.getHeight());
					heldPanel.setWidth(wWidth / 2);
					heldPanel.setHeight((wHeight - terminal.getHeight()) / 2);
					;
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = wWidth / 2;
					heldPanel.resizeY = (wHeight - terminal.getHeight()) / 2 + terminal.getHeight();
					heldPanel.resizeWidth = wWidth / 2;
					heldPanel.resizeHeight = (wHeight - terminal.getHeight()) / 2;
				}
			}
			// Just right side
			else {
				if (released) {
					heldPanel.setX(wWidth / 2);
					heldPanel.setY(terminal.getHeight());
					heldPanel.setWidth(wWidth / 2);
					heldPanel.setHeight(wHeight - terminal.getHeight());
					;
				}
				else {
					heldPanel.resizing = true;
					heldPanel.resizeX = wWidth / 2;
					heldPanel.resizeY = wHeight;
					heldPanel.resizeWidth = wWidth / 2;
					heldPanel.resizeHeight = wHeight - terminal.getHeight();
				}
			}
		}
		// Bottom Side
		else if (y < 10) {
			if (released) {
				heldPanel.setX(0);
				heldPanel.setY(terminal.getHeight());
				heldPanel.setWidth(wWidth);
				heldPanel.setHeight((wHeight - terminal.getHeight()) / 2);
			}
			else {
				heldPanel.resizing = true;
				heldPanel.resizeX = 0;
				heldPanel.resizeY = (wHeight - terminal.getHeight()) / 2 + terminal.getHeight();
				heldPanel.resizeWidth = wWidth;
				heldPanel.resizeHeight = (wHeight - terminal.getHeight()) / 2;
			}
		}
		// Top side
		else if (y > wHeight - 10) {
			if (released) {
				heldPanel.setX(0);
				heldPanel.setY((wHeight - terminal.getHeight()) / 2 + terminal.getHeight());
				heldPanel.setWidth(wWidth);
				heldPanel.setHeight((wHeight - terminal.getHeight()) / 2);
			}
			else {
				heldPanel.resizing = true;
				heldPanel.resizeX = 0;
				heldPanel.resizeY = wHeight;
				heldPanel.resizeWidth = wWidth;
				heldPanel.resizeHeight = (wHeight - terminal.getHeight()) / 2;
			}
		}
		else
			heldPanel.resizing = false;
	}


	/**
	 * Resize existing panels due to a window resizing event
	 * 
	 * @param w
	 * @param h
	 */
	public void resizePanels(int w, int h) {
		head.setWidth(w);
		head.setHeight(h);
		
		head.reflowSplits();
	}



	/**
	 * Setup panels for using scheme functionality
	 */
	public void initCADWindows() {
		int w = FluxCadd.backend.getWidth();
		int h = FluxCadd.backend.getHeight();
		
		Panel terminal = new Panel("terminal");

		Panel previewWindow = new Panel(0,0,w,h);
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);

		Panel codeWindow = new Panel(0,0,w,h);
		codeWindow.content = new Content_Scheme(codeWindow, (Content_View) previewWindow.content);
		
		head = previewWindow;
		head.split(Panel.SplitState.HORIZONTAL, terminal);
		head.getChild(0).split(Panel.SplitState.VERTICAL, codeWindow);
	}


	/**
	 * Setup panels for using SDF rendering functionality
	 */
	public void initSDFWindows() {
		int w = FluxCadd.backend.getWidth();
		int h = FluxCadd.backend.getHeight();
		
		Panel terminal = new Panel("terminal");

		Panel previewWindow = new Panel(0,0,w,h);
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		previewWindow.moveable = false;
		previewWindow.resizable = false;
		
		Panel animationWindow = new Panel(0,0,w,h);
		animationWindow.content = new Content_Animation(animationWindow);
		animationWindow.moveable = false;
		animationWindow.resizable = false;
		animationWindow.maximumHeight = 200;
		
		Panel controlWindow = new Panel(0,0,w,h);
		controlWindow.content = new Content_Renderer(controlWindow, (Content_View) previewWindow.content, (Content_Animation) animationWindow.content);
		controlWindow.moveable = false;
		controlWindow.resizable = false;
		controlWindow.maximumWidth = 500;
		
		head = previewWindow;
		head.split(Panel.SplitState.HORIZONTAL, animationWindow);
		head.getChild(1).split(Panel.SplitState.HORIZONTAL, terminal);
		head.getChild(0).split(Panel.SplitState.VERTICAL, controlWindow);
		
		//head.printTree(0);
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
		
		head = chooserWindow;
	}
}
