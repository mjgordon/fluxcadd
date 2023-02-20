package graphics;

import org.lwjgl.opengl.GL11;

public class Primitives {
	
	public static void rect(int x, int y, int width, int height) {
		if (OGLWrapper.colorFill != null) {
			OGLWrapper.glColor(OGLWrapper.colorFill);
			GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + width, y);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x, y + height);
			GL11.glEnd();
		}
		if (OGLWrapper.colorStroke != null) {
			OGLWrapper.glColor(OGLWrapper.colorStroke);
			GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x + width, y);
			GL11.glVertex2i(x + width, y + height);
			GL11.glVertex2i(x, y + height);
			GL11.glEnd();
		}
	}

	public static void line(int x, int y, int x2, int y2) {
		if (OGLWrapper.colorStroke != null) {
			OGLWrapper.glColor(OGLWrapper.colorStroke);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2i(x, y);
			GL11.glVertex2i(x2, y2);
			GL11.glEnd();	
		}
	}
	
	public static void line(double x, double y, double x2, double y2) {
		if (OGLWrapper.colorStroke != null) {
			OGLWrapper.glColor(OGLWrapper.colorStroke);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex2d(x, y);
			GL11.glVertex2d(x2, y2);
			GL11.glEnd();	
		}
	}

}
