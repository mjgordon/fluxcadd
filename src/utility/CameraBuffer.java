package utility;

/*
 * Contains camera data for use outside the camera; i.e. in mouse picking code. 
 */

import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector3d;
import org.lwjgl.BufferUtils;

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
	
	
	/**
	 * From screen to world
	 * Original unproject code by Luke Benstead. Converted for LWJGL by Crede.
	 * @param mouseX
	 * @param mouseY
	 * @return
	 */
	public Vector3d unproject(int mouseX,int mouseY,float z) {
		FloatBuffer position = BufferUtils.createFloatBuffer(3);
		winX = (float) mouseX;
		winY = (float) mouseY;
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		//TODO: FEATURE : Unproject
//		GLU.gluUnProject(winX, winY, z,
//				modelview, 
//				projection,
//				viewport,
//				position);
		return new Vector3d(position.get(0),position.get(1),position.get(2));
	}
	
	/**
	 * From world to screen
	 * @param worldPosition
	 * @return
	 */
	public Vector3d project(Vector3d worldPosition) {
		FloatBuffer screen = BufferUtils.createFloatBuffer(3);
		
		//TODO: FEATURE : project
		//GLU.gluProject(worldPosition.x, worldPosition.y, worldPosition.z, modelview, projection, viewport, screen);
		
		Vector3d out = new Vector3d(screen.get(0), screen.get(1), screen.get(2));
		return(out);
	}
}



