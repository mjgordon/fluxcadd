package graphics;

import org.lwjgl.opengl.GL11;

import utility.Color;
import utility.PVector;

/**
 * Direct interface for calling common opengl functions with internal vector, color etc classes. 
 *
 */
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
		
		GL11.glColor3f(r, g, b);
	}

	public static void glColor(Color c, int a) {
		float r = c.r / 255.0f;
		float g = c.g / 255.0f;
		float b = c.b / 255.0f;
		
		GL11.glColor4f(r, g, b, a / 255.0f);
	}
}
