package main;

import java.nio.IntBuffer;

import javax.swing.UIManager;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import graphics.Graphics2D;
import graphics.Graphics3D;
import io.*;
import ui.PanelManager;


public class FluxCadd {

	public static PanelManager panelManager;
	
	/**
	 *  The window handle identifier, used for glfw functions etc
	 */
	public static long glidWindow;

	private static int width = 1920; // 1920 |  1600
	private static int height = 1027; // 1027 | 800

	/**
	 * Set to true if an animation is being drawn that needs to be redrawn every
	 * frame Set to false to block for input
	 */
	public static boolean animating = false;
	
	/**
	 * Set to true to redraw once
	 */
	public static boolean forceRedraw = false;
	
	public static long cursorArrow;
	public static long cursorResizeH;
	public static long cursorResizeV;


	public static void main(String[] argv) {
		// Setup OpenGL/GLFW 
		init();
		
		// Load user settings
		try {
			Config.loadTextFile("config/config.txt");	
		}
		catch (java.io.IOException e) {
			try {
				Config.loadTextFile("config/config_default.txt");	
			}
			catch(java.io.IOException e2) {
				System.out.println("Couldn't load 'config/config.txt' or config/config_default.txt'");
				System.exit(1);
			}
		}
		
		
		// Match JavaX Swing ui elements to the native OS styling
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		panelManager = new PanelManager();

		// panelManager.initCAMWindows();
		panelManager.initSDFWindows();
		
		loop();
	}
	
	
	@SuppressWarnings("static-access")
	private static void init() {
		System.out.println("Using LWJGL " + Version.getVersion());

		GLFWErrorCallback.createPrint(System.err).set();

		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}
		
		// Create the window
		glidWindow = GLFW.glfwCreateWindow(width, height, "FluxCADD", MemoryUtil.NULL, MemoryUtil.NULL);
		if (glidWindow == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		// Configure our window
		GLFW.glfwDefaultWindowHints();
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 4);
		GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden immediately after creation
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); 

		setupInputCallbacks();

		// Get the resolution of the primary monitor and center our window
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		GLFW.glfwSetWindowPos(glidWindow, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

		// Make the OpenGL context current
		GLFW.glfwMakeContextCurrent(glidWindow);

		GL.createCapabilities();
		
		// TODO: Recheck this is being set up correctly
		Callback hello = GLUtil.setupDebugMessageCallback(System.out);
		
		GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);

		// Enable v-sync
		GLFW.glfwSwapInterval(1);

		// Make the window visible
		GLFW.glfwShowWindow(glidWindow);
		
		// Enable Transparency (Watch out for this)
		GL33.glEnable(GL33.GL_BLEND);
		GL33.glBlendFunc(GL33.GL_SRC_ALPHA, GL33.GL_ONE_MINUS_SRC_ALPHA);

		GL33.glClearColor(0.4f, 0.4f, 1, 1);
		
		// Create cursors
		cursorArrow = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
		cursorResizeH = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
		cursorResizeV = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);
		
		Graphics2D.setup();
		Graphics3D.setup();
	}


	private static void stop() {
		// Free the window callbacks and destroy the window
		Callbacks.glfwFreeCallbacks(glidWindow);
		GLFW.glfwDestroyWindow(glidWindow);

		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}


	@SuppressWarnings("static-access")
	private static void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		GL33.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!GLFW.glfwWindowShouldClose(glidWindow)) {
			GL33.glClear(GL33.GL_COLOR_BUFFER_BIT | GL33.GL_DEPTH_BUFFER_BIT);
			
			if (animating || forceRedraw) {
				GLFW.glfwPollEvents();
				forceRedraw = false;
			}
			else {
				/*
				 * Note: this seems to be getting returned by _some_ event roughly every ~1 second
				 * while the mouse is over the window, even when no callbacks are returned. 
				 */
				GLFW.glfwWaitEvents();
			}
			
			GL33.glViewport(0, 0, width, height);
			
			FluxCadd.panelManager.render();

			// Swap the color buffers
			GLFW.glfwSwapBuffers(glidWindow);
		}
		
		stop();
	}


	private static void setupInputCallbacks() {
		final Keyboard keyboard = Keyboard.instance();
		final TextInput textInput = TextInput.instance();
		final MouseButton mouseButton = MouseButton.instance();
		final MouseCursor mouseCursor = MouseCursor.instance();
		final MouseWheel mouseWheel = MouseWheel.instance();

		// Keys (individual)
		GLFW.glfwSetKeyCallback(glidWindow, (window, key, scancode, action, mods) -> {
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE) {
				GLFW.glfwSetWindowShouldClose(window, true);
			}
				
			KeyboardEvent e = new KeyboardEvent(key, action);
			keyboard.keyboardEvent(e);
			
			panelManager.keyPressed(e);
		});

		// Keys (text input)
		GLFW.glfwSetCharCallback(glidWindow, (window, codepoint) -> {
			TextInputEvent e = new TextInputEvent((char) codepoint);
			textInput.textInputEvent(e);
			
			panelManager.textInput(e);
		});

		// Mouse Presses
		GLFW.glfwSetMouseButtonCallback(glidWindow, (window, button, action, mods) -> {
			MouseButtonEvent.Type type = (action == GLFW.GLFW_PRESS) ? MouseButtonEvent.Type.PRESSED : MouseButtonEvent.Type.RELEASED;
			MouseButtonEvent e = new MouseButtonEvent(mouseCursor.getX(), mouseCursor.getY(), button, type);
			mouseButton.mouseButtonEvent(e);
			
			panelManager.mouseButton(e);
		});

		// Mouse Movement
		GLFW.glfwSetCursorPosCallback(glidWindow, (window, xpos, ypos) -> {
			MouseCursorEvent e = new MouseCursorEvent((int) xpos, (int) ypos);
			mouseCursor.mouseCursorEvent(e);
			
			panelManager.mouseCursor(e);
		});


		// Mousewheel
		GLFW.glfwSetScrollCallback(glidWindow, (window, dx, dy) -> {
			MouseWheelEvent e = new MouseWheelEvent(MouseCursor.instance().getX(), MouseCursor.instance().getY(),(int) dx, (int) dy);
			mouseWheel.mouseWheelEvent(e);
			
			panelManager.mouseWheel(e);
		});

		// Window Resize
		GLFW.glfwSetWindowSizeCallback(glidWindow, (window, w, h) -> {
			width = w;
			height = h;
			
			// Minimized
			if (width == 0 || height == 0) {
				return;
			}
						
			if (FluxCadd.panelManager != null) {
				FluxCadd.panelManager.resizePanels(w, h);
			}
			forceRedraw = true;
		});
		
		
		// Window Maximize Toggle
		GLFW.glfwSetWindowMaximizeCallback(glidWindow, (window, maximized) -> {
			try (MemoryStack stack = MemoryStack.stackPush()) {
			    IntBuffer newWidth = stack.mallocInt(1);
			    IntBuffer newHeight = stack.mallocInt(1);
			    GLFW.glfwGetWindowSize(window, newWidth, newHeight);
			    width = newWidth.get();
			    height = newHeight.get();
			}
			if (FluxCadd.panelManager != null) {
				FluxCadd.panelManager.resizePanels(width, height);
			}
			forceRedraw = true;
		});
	}


	public static int getWidth() {
		return width;
	}


	public static int getHeight() {
		return height;
	}
	
	
	public static void setCursor(long cursorId) {
		GLFW.glfwSetCursor(glidWindow, cursorId);
	}
}