package fonts;

import org.lwjgl.opengl.GL11;

import utility.Color3i;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

import javax.imageio.*;

public class BitmapFont {

	private final static int width = 128;
	private final static int height = 192;

	private final static int atlasWidth = 16;

	public static final int cellWidth = 8;
	public static final int cellHeight = 12;
	
	private static double charU = 1.0;
	private static double charV = 1.0;

	private static int textureIdBlack;
	private static int textureIdWhite;


	public static void initialize() {
		BufferedImage image;

		try {
			image = ImageIO.read(new File("data/font.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		ByteBuffer imageBufferBlack = imageToBufferBW(image, Color3i.black);
		ByteBuffer imageBufferWhite = imageToBufferBW(image, Color3i.white);
		
		textureIdBlack = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIdBlack);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBufferBlack);
		
		textureIdWhite = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIdWhite);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBufferWhite);
		
		charU = 1.0 * cellWidth / width;
		charV = 1.0 * cellHeight / height;
	}


	public static void drawString(String s, int x, int y, boolean black) {
		char[] charArray = s.toCharArray();
		int originalX = x;
		
		if (black) {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIdBlack);
		}
		else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureIdWhite);
		}
		
		
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if (c == '\n') {
				x = originalX;
				y += 12;
				continue;
			}
			
			double u = (c % atlasWidth) * 1.0 * charU;
			double v = (c / atlasWidth) * 1.0 * charV;
			
			GL11.glPushMatrix();
			{
				GL11.glTranslatef(x, y, 0);
				
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glTexCoord2d(u,v); 
				GL11.glVertex2d(0, 0);
				GL11.glTexCoord2d(u + charU,v); 
				GL11.glVertex2d(cellWidth, 0);
				GL11.glTexCoord2d(u + charU,v + charV); 
				GL11.glVertex2d(cellWidth, cellHeight);
				GL11.glTexCoord2d(u,v + charV); 
				GL11.glVertex2d(0, cellHeight);

				GL11.glEnd();
				
				GL11.glDisable(GL11.GL_TEXTURE_2D);
			}
			GL11.glPopMatrix();

			x += 8;
		}
	}
	
	
	/**
	 * Loads a black and white image as a font, with white pixels being filled and black pixels being transparent
	 * @param image
	 * @return
	 */
	private static ByteBuffer imageToBufferBW(BufferedImage image, Color3i color) {
	    int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
	    ByteBuffer buffer = ByteBuffer.allocateDirect(pixels.length * 4);
	    
	    for (int pixel : pixels) {
	    	boolean filled = (pixel & 0xFF) > 0;
	    	if (filled) {
	    		buffer.put((byte) color.r);
	    		buffer.put((byte) color.g);
	    		buffer.put((byte) color.b);
	    		buffer.put((byte) 255);
	    	}
	    	else {
	    		buffer.put((byte) 0);
	    		buffer.put((byte) 0);
	    		buffer.put((byte) 0);
	    		buffer.put((byte) 0);
	    	}
	    }
	    buffer.flip();
	    return buffer;
	}
	
	
	private static ByteBuffer imageToBuffer(BufferedImage image) {
	    int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
	    ByteBuffer buffer = ByteBuffer.allocateDirect(pixels.length * 4);
	    
	    for (int pixel : pixels) {
	        buffer.put((byte) ((pixel >> 16) & 0xFF));
	        buffer.put((byte) ((pixel >> 8) & 0xFF));
	        buffer.put((byte) (pixel & 0xFF));
	        buffer.put((byte) ((pixel >> 24) & 0xFF));
	    }
	    buffer.flip();
	    return buffer;
	}
}
