package graphics;

import java.nio.FloatBuffer;

import input.*;
import main.FluxCadd;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Window_LWJGL extends FluxCaddWindow {

	// The window handle
	// TODO: de-static this
	public static long window;

	private int width;
	private int height;

	@Override
	public void init() {
		System.out.println("Using LWJGL " + Version.getVersion());
		
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints(); // optional, the current window hints are
									// already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden
													// after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be
													// resizable
		
		width = 1024;
		height = 768;

		// Create the window
		window = glfwCreateWindow(width, height, "FluxCADD", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		setupInputCallbacks();

		// Get the resolution of the primary monitor
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		// Center our window
		glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		
		GL.createCapabilities();
		
		// Enable v-sync
		glfwSwapInterval(1);

		// Make the window visible
		glfwShowWindow(window);
		
		//Enable Transparency (Watch out for this)
		glEnable (GL_BLEND);
		glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		//Setup Lights
		glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
		glEnable(GL_COLOR_MATERIAL);
		glDisable(GL_CULL_FACE);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		FloatBuffer lightAmbient = BufferUtils.createFloatBuffer(4).put(
				new float[] { 0.1f, 0.1f, 0.1f, 1.0f });
		FloatBuffer lightDiffuse = BufferUtils.createFloatBuffer(4).put(
				new float[] { 0.5f, 0.5f, 0.5f, 1.0f });
		FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4).put(
				new float[] { 50f, 50f, 50f, 1.0f });
		lightAmbient.rewind();
		lightPosition.rewind();
		lightDiffuse.rewind();

		glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);
		glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient);
		glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
		glDisable(GL_LIGHTING);
		
		
		//Set basic projection information
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, width, 0, height, 1, -1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	

	@Override
	public void stop() {
		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
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
		while (!glfwWindowShouldClose(window)) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the
																// framebuffer

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
			
			FluxCadd.panelManager.render();
			
			glfwSwapBuffers(window); // swap the color buffers
		}
	}
	
	private void setupInputCallbacks() {
		Keyboard keyboard = Keyboard.instance();
		TextInput textInput = TextInput.instance();
		MouseButton mouseButton = MouseButton.instance();
		MouseCursor mouseCursor = MouseCursor.instance();
		MouseWheel mouseWheel = MouseWheel.instance();

		//Keys (individual)
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {

			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true);

			KeyboardEvent e = new KeyboardEvent(key, action);
			keyboard.keyboardEvent(e);
		});

		//Keys (text input)
		glfwSetCharCallback(window, (window,codepoint) -> {
			TextInputEvent e = new TextInputEvent(codepoint);
			textInput.textInputEvent(e);
		});
		
		//Mouse Presses
		glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
			MouseButtonEvent.Type type = (action == GLFW_PRESS) ? MouseButtonEvent.Type.PRESSED : MouseButtonEvent.Type.RELEASED;
			MouseButtonEvent e = new MouseButtonEvent(button, type);
			mouseButton.mouseButtonEvent(e);
		});

		//Mouse Movement
		glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			//TODO: make an official position on y-flip
			ypos = height - ypos;
			MouseCursorEvent e = new MouseCursorEvent(xpos,ypos);
			mouseCursor.mouseCursorEvent(e);
		});

		//Mousewheel
		glfwSetScrollCallback(window, (window, dx, dy) -> {
			MouseWheelEvent e = new MouseWheelEvent((int)dx,(int)dy);
			mouseWheel.mouseWheelEvent(e);
		});
	}

	public int getWidth() {
		return (width);
	}

	public int getHeight() {
		return (height);
	}

	// IntBuffer w = BufferUtils.createIntBuffer(1);
	// IntBuffer h = BufferUtils.createIntBuffer(1);
	// glfwGetWindowSize(window, w, h);
	// int width = w.get(0);
	// int height = h.get(0);

	
	
}