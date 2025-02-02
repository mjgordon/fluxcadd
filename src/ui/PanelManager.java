package ui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.glfw.GLFW;

import event.*;
import io.*;
import main.FluxCadd;
import render_sdf.animation.Content_Animation;
import render_sdf.renderer.Content_Renderer;
import scheme.Content_Scheme;


/**
 * Primary UI Manager. Stores a list of panels (sub-windows) and is responsible
 * for sending user input to them
 */
public final class PanelManager implements EventListener {
	private Panel head;

	private Panel terminal;
	
	private Panel activePanel = null;
	
	private Panel draggedPanel = null;


	public PanelManager() {
		head = new Panel("terminal");

		Keyboard.instance().register(this);
		TextInput.instance().register(this);
		MouseButton.instance().register(this);
		MouseCursor.instance().register(this);
		MouseWheel.instance().register(this);
	}


	/**
	 * Main entry point for rendering all FluxCadd content
	 * Due to this, the y-flipping scaling is applied here before all other operations
	 */
	public void render() {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, FluxCadd.getHeight(), 0);
		GL11.glScalef(1,-1, 1);	
		
		head.render(activePanel);
		
		GL11.glPopMatrix();
	}


	@Override
	public void message(EventMessage message) {
		// TODO make this a switch statement (requires Java21)
		
		if (message instanceof KeyboardEvent) {
			KeyboardEvent event = (KeyboardEvent) message;
			if (event.type == GLFW.GLFW_PRESS) {
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
			else {
				mouseMoved((int) event.x, (int) event.y);
			}
		}
		else if (message instanceof MouseWheelEvent) {
			MouseWheelEvent event = (MouseWheelEvent) message;
			mouseWheel(0, event.dy);
		}
	}
	
	
	private void mouseMoved(int x, int y) {
		draggedPanel = head.pickBorder(x, y);
		
		if (draggedPanel == null) {
			FluxCadd.setCursor(FluxCadd.cursorArrow);
		}
		else if (draggedPanel.splitState == Panel.SplitState.HORIZONTAL) {
			FluxCadd.setCursor(FluxCadd.cursorResizeH);
		}
		else if (draggedPanel.splitState == Panel.SplitState.VERTICAL) {
			FluxCadd.setCursor(FluxCadd.cursorResizeV);
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
		
		if (activePanel != null) {
			activePanel.mouseReleased(button);
		}
	}


	private void mouseDragged(int button, int x, int y, int dx, int dy) {
		if (draggedPanel != null) {
			dragPanelBorder(x, y);
		}
		
		else if (activePanel != null) {
			activePanel.mouseDragged(button, dx, dy);
		}	
	}


	private void mouseWheel(int dx, int dy) {
		Panel scrollPanel = head.pick(MouseCursor.instance().getX(), MouseCursor.instance().getY());
		if (scrollPanel != null) {
			scrollPanel.content.mouseWheel(dy);
		}
	}
	
	
	/**
	 * Called from a mouse drag event, attempts to move the held panel split border if possible
	 * @param mouseX
	 * @param mouseY
	 */
	private void dragPanelBorder(int mouseX, int mouseY) {
		Panel childA = draggedPanel.children.get(draggedPanel.resizeSelected);
		Panel childB = draggedPanel.children.get(draggedPanel.resizeSelected + 1);
		
		if (draggedPanel.splitState == Panel.SplitState.HORIZONTAL) {
			int proposedWidthA = mouseX - childA.positionX;
			int proposedWidthB = (childB.positionX + childB.width) - mouseX;
			
			if (proposedWidthA < childA.minimumWidth) {
				int diff = childA.minimumWidth - proposedWidthA;
				proposedWidthA += diff;
				proposedWidthB -= diff;
			}
			if (proposedWidthB < childB.minimumWidth) {
				int diff = childB.minimumWidth - proposedWidthB;
				proposedWidthA -= diff;
				proposedWidthB += diff;
			}
			if (childA.maximumWidth != -1 && proposedWidthA > childA.maximumWidth) {
				int diff = proposedWidthA - childA.maximumWidth;
				proposedWidthA -= diff;
				proposedWidthB += diff;
			}
			if (childB.maximumWidth != -1 && proposedWidthB > childB.maximumWidth) {
				int diff = proposedWidthB - childB.maximumWidth;
				proposedWidthA += diff;
				proposedWidthB -= diff;
			}
			
			childA.reflowSplits(proposedWidthA, childA.height);
			childB.reflowSplits(proposedWidthB, childB.height);
			childB.positionX = childA.positionX + childA.width;
		}
		else if (draggedPanel.splitState == Panel.SplitState.VERTICAL) {
			int proposedHeightA = mouseY - childA.positionY;
			int proposedHeightB = (childB.positionY + childB.height) - mouseY; 
			
			if (proposedHeightA < childA.minimumHeight) {
				int diff = childA.minimumHeight - proposedHeightA;
				proposedHeightA += diff;
				proposedHeightB -= diff;
			}
			if (proposedHeightB < childB.minimumHeight) {
				int diff = childB.minimumHeight - proposedHeightB;
				proposedHeightA -= diff;
				proposedHeightB += diff;
			}
			if (childA.maximumHeight != -1 && proposedHeightA > childA.maximumHeight) {
				int diff = proposedHeightA - childA.maximumHeight;
				proposedHeightA -= diff;
				proposedHeightB += diff;
			}
			if (childB.maximumHeight != -1 && proposedHeightB > childB.maximumHeight) {
				int diff = proposedHeightB - childB.maximumHeight;
				proposedHeightA += diff;
				proposedHeightB -= diff;
			}
			
			childA.reflowSplits(childA.width, proposedHeightA);
			childB.reflowSplits(childB.width, proposedHeightB);
			childB.positionY = childA.positionY + childA.height;
		}
	}


	/**
	 * Resize existing panels due to a window resizing event
	 * 
	 * @param w
	 * @param h
	 */
	public void resizePanels(int w, int h) {
		head.reflowSplits(w, h);
		head.printTree(0);
	}



	/**
	 * Setup panels for using scheme functionality
	 */
	public void initCADWindows() {
		int w = FluxCadd.getWidth();
		int h = FluxCadd.getHeight();
		
		Panel terminal = new Panel("terminal");

		Panel previewWindow = new Panel(0,0,w,h);
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);

		Panel codeWindow = new Panel(0,0,w,h);
		codeWindow.content = new Content_Scheme(codeWindow, (Content_View) previewWindow.content);
		
		head = previewWindow;
		head.split(Panel.SplitState.VERTICAL, terminal);
		head.getChild(0).split(Panel.SplitState.HORIZONTAL, codeWindow);
	}


	/**
	 * Setup panels for using SDF rendering functionality
	 */
	public void initSDFWindows() {
		int w = FluxCadd.getWidth();
		int h = FluxCadd.getHeight();
		
		// First define all panels
		Panel terminal = new Panel("terminal");

		Panel previewWindow = new Panel(0,0,w,h);
		previewWindow.content = new Content_View(previewWindow, ViewType.PERSP);
		
		Panel animationWindow = new Panel(0,0,w,h);
		animationWindow.content = new Content_Animation(animationWindow);
		animationWindow.maximumHeight = 200;
		
		Panel controlWindow = new Panel(0,0,w,h);
		controlWindow.content = new Content_Renderer(controlWindow, (Content_View) previewWindow.content, (Content_Animation) animationWindow.content);
		controlWindow.maximumWidth = 500;
		
		// Then set them as splits
		head = previewWindow;
		head.split(Panel.SplitState.VERTICAL, animationWindow);
		head.getChild(1).split(Panel.SplitState.VERTICAL, terminal);
		head.getChild(0).split(Panel.SplitState.HORIZONTAL, controlWindow);
		
		head.printTree(0);
	}


	/**
	 * Setup panels to choose desired workspace
	 */
	public void initChooser() {
		int w = FluxCadd.getWidth();
		int h = FluxCadd.getHeight();

		Panel chooserWindow = new Panel(0, terminal.height, w, h - terminal.height);
		chooserWindow.content = new Content_Chooser(chooserWindow);
		chooserWindow.windowTitle = "Workspace Chooser";
		
		head = chooserWindow;
	}
}
