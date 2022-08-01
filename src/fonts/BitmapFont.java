package fonts;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import geometry.PointCloud;
import utility.Color;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.*;

public class BitmapFont {

	public final static int width = 128;
	public final static int height = 192;

	public final static int atlasWidth = 16;

	public static final int cellWidth = 8;
	public static final int cellHeight = 12;

	public static ArrayList<PointCloud> clouds = new ArrayList<PointCloud>();


	public static void initialize() {

		BufferedImage image;

		try {
			image = ImageIO.read(new File("data/font.png"));

			int[] pixels = new int[width * height];
			image.getRGB(0, 0, width, height, pixels, 0, width);

			for (int cy = 0; cy < atlasWidth; cy++) {
				int py = cy * cellHeight;
				for (int cx = 0; cx < atlasWidth; cx++) {
					int px = cx * cellWidth;

					PointCloud cloud = new PointCloud();
					for (int i = 0; i < (cellWidth * cellHeight); i++) {
						int lx = i % cellWidth;
						int ly = i / cellWidth;
						int pid = ((py + ly) * width + (px + lx));

						int r = pixels[pid] >> 16 & 0xFF;

						if (r > 0) {
							cloud.addPoint(new Vector3d(lx, ly, 1));
						}
					}

					clouds.add(cloud);
					// System.out.println(cloud.positions.size());
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static void drawString(String s, int x, int y, Color colorOverride) {
		char[] charArray = s.toCharArray();
		int originalX = x;
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if (c == '\n') {
				x = originalX;
				y += 12;
				continue;
			}
			GL11.glPushMatrix();
			GL11.glTranslatef(x, y, 0);

			if (colorOverride != null) {
				clouds.get(c).render2d(colorOverride);
			}
			else {
				clouds.get(c).render2d();
			}

			GL11.glPopMatrix();

			x += 8;
		}
	}

}
