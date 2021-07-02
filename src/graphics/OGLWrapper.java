package graphics;

import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;

import org.lwjgl.opengl.GL11;

import utility.Color;
import utility.PVector;

public class OGLWrapper {
	public static void glVertex(PVector v) {
		GL11.glVertex3f(v.x, v.y,v.z);
	}
	
	public static void glNormal(PVector v) {
		GL11.glNormal3f(v.x, v.y,v.z);
	}

	public static void glColor(Color c) {
		float r = c.r / 255.0f;
		float g = c.g / 255.0f;
		float b = c.b / 255.0f;
		
		glColor3f(r, g, b);
	}

	public static void glColor(Color c, int a) {
		float r = c.r / 255.0f;
		float g = c.g / 255.0f;
		float b = c.b / 255.0f;
		
		glColor4f(r, g, b, a / 255.0f);
	}
}
