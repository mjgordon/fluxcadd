package main;

import io.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import fonts.BitmapFont;


import static org.lwjgl.opengl.GL11.*;


public class Backend_LWJGL {

	// The window handle
	public static long window;

	private int width = 1920; // 1920 |  1600
	private int height = 1027; // 1027 | 800

	/**
	 * Set to true if an animation is being drawn that needs to be redrawn every
	 * frame Set to false to block for input
	 */
	public boolean animating = true;
	
	/**
	 * Set to true to redraw once
	 */
	public boolean forceRedraw = false;
	
	public long cursorArrow;
	public long cursorResizeH;
	public long cursorResizeV;
	
	
	public void init() {
		System.out.println("Using LWJGL " + Version.getVersion());
		System.out.println("Max ModelView Stack Depth " + GL11.GL_MAX_MODELVIEW_STACK_DEPTH);

		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		// Configure our window
		GLFW.glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE); // the window will stay hidden
													// after creation
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_TRUE); // the window will be
													// resizable

		// Create the window
		window = GLFW.glfwCreateWindow(width, height, "FluxCADD", MemoryUtil.NULL, MemoryUtil.NULL);
		if (window == MemoryUtil.NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		setupInputCallbacks();

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
		// Center our window
		GLFW.glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

		// Make the OpenGL context current
		GLFW.glfwMakeContextCurrent(window);

		GL.createCapabilities();

		// Enable v-sync
		GLFW.glfwSwapInterval(1);

		// Make the window visible
		GLFW.glfwShowWindow(window);

		// Enable Transparency (Watch out for this)
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// Setup Lights
		glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
		glEnable(GL_COLOR_MATERIAL);
		glDisable(GL_CULL_FACE);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4).put(new float[] { 0.1f, 0.1f, 0.1f, 1.0f });
		FloatBuffer lightDiffuse = BufferUtils.createFloatBuffer(4).put(new float[] { 0.5f, 0.5f, 0.5f, 1.0f });
		FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4).put(new float[] { 50f, 50f, 50f, 1.0f });
		lightAmbient.rewind();
		lightPosition.rewind();
		lightDiffuse.rewind();

		glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);
		glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient);
		glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
		glDisable(GL_LIGHTING);

		glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);

		// Set basic projection information
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, 0, height, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glClearColor(0.4f, 0.4f, 1, 1);

		// Load Font
		BitmapFont.initialize();
		
		// Create cursors
		cursorArrow = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
		cursorResizeH = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HRESIZE_CURSOR);
		cursorResizeV = GLFW.glfwCreateStandardCursor(GLFW.GLFW_VRESIZE_CURSOR);

	}


	public void stop() {
		// Free the window callbacks and destroy the window
		Callbacks.glfwFreeCallbacks(window);
		GLFW.glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}


	public void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		while (!GLFW.glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
			
			if (animating || forceRedraw) {
				GLFW.glfwPollEvents();
				forceRedraw = false;
			}
			else {
				GLFW.glfwWaitEvents();
			}
			
			GL11.glViewport(0, 0, width, height);
			
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			//TODO: We could possibly move flipping to here? 
			glOrtho(0, width, 0, height, -1, 1);

			FluxCadd.panelManager.render();

			// Swap the color buffers
			GLFW.glfwSwapBuffers(window);
		}
	}


	private void setupInputCallbacks() {
		Keyboard keyboard = Keyboard.instance();
		TextInput textInput = TextInput.instance();
		MouseButton mouseButton = MouseButton.instance();
		MouseCursor mouseCursor = MouseCursor.instance();
		MouseWheel mouseWheel = MouseWheel.instance();

		// Keys (individual)
		GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {

			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
				GLFW.glfwSetWindowShouldClose(window, true);

			KeyboardEvent e = new KeyboardEvent(key, action);
			keyboard.keyboardEvent(e);
		});

		// Keys (text input)
		GLFW.glfwSetCharCallback(window, (window, codepoint) -> {
			TextInputEvent e = new TextInputEvent((char) codepoint);
			textInput.textInputEvent(e);
		});

		// Mouse Presses
		GLFW.glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			MouseButtonEvent.Type type = (action == GLFW.GLFW_PRESS) ? MouseButtonEvent.Type.PRESSED : MouseButtonEvent.Type.RELEASED;
			MouseButtonEvent e = new MouseButtonEvent(button, type);
			mouseButton.mouseButtonEvent(e);
		});

		// Mouse Movement
		GLFW.glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			MouseCursorEvent e = new MouseCursorEvent(xpos, ypos);
			mouseCursor.mouseCursorEvent(e);
		});

		// Mousewheel
		GLFW.glfwSetScrollCallback(window, (window, dx, dy) -> {
			MouseWheelEvent e = new MouseWheelEvent((int) dx, (int) dy);
			mouseWheel.mouseWheelEvent(e);
		});

		// Window Resize
		GLFW.glfwSetWindowSizeCallback(window, (window, w, h) -> {
			this.width = w;
			this.height = h;
						
			if (FluxCadd.panelManager != null) {
				FluxCadd.panelManager.resizePanels(w, h);
			}
			forceRedraw = true;
		});
		// Window Maximize Toggle
		GLFW.glfwSetWindowMaximizeCallback(window, (window, maximized) -> {
			try (var stack = MemoryStack.stackPush()) {
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


	public int getWidth() {
		return width;
	}


	public int getHeight() {
		return height;
	}
	
	
	public void setCursor(long cursorId) {
		GLFW.glfwSetCursor(window, cursorId);
	}
}