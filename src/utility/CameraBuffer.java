package utility;

import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;


/**
 * Contains camera data for use outside the camera; i.e. in mouse picking code. 
 */
public class CameraBuffer {
	public IntBuffer viewport = BufferUtils.createIntBuffer(16);
	public FloatBuffer modelview = BufferUtils.createFloatBuffer(16);
	public FloatBuffer projection = BufferUtils.createFloatBuffer(16);
	public FloatBuffer winZ = BufferUtils.createFloatBuffer(1);
	public float winX, winY;
	
	
	public void update() {	
		GL11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, modelview);
		GL11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, projection);
		GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);
	}
}



