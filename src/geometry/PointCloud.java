package geometry;

import org.joml.Matrix4d;
import org.joml.Vector2d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;
import intersection.Intersection;
import iofile.Plaintext;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import utility.Color;

public class PointCloud extends Geometry {

	public ArrayList<Vector3d> positions;
	public ArrayList<Color> colors;
	public ArrayList<Vector3d> normals;

	public int pointSize = 1;


	public PointCloud() {
		positions = new ArrayList<Vector3d>();
		colors = new ArrayList<Color>();
		normals = new ArrayList<Vector3d>();

		colorFill = new Color(0, 0, 0);

	}


	public PointCloud(String filepath) {
		String[] input = Plaintext.loadPlaintext(filepath);

		positions = new ArrayList<Vector3d>();
		colors = new ArrayList<Color>();
		normals = new ArrayList<Vector3d>();

		for (String s : input) {
			String[] parts = s.split(" ");

			float x = Float.valueOf(parts[0]);
			float y = Float.valueOf(parts[1]);
			float z = Float.valueOf(parts[2]);

			int r = Integer.valueOf(parts[3]);
			int g = Integer.valueOf(parts[4]);
			int b = Integer.valueOf(parts[5]);

			float u = Float.valueOf(parts[6]);
			float v = Float.valueOf(parts[7]);
			float w = Float.valueOf(parts[8]);

			positions.add(new Vector3d(x, y, z));
			colors.add(new Color(r, g, b));
			normals.add(new Vector3d(u, v, w));
		}

		colorFill = null;
	}


	@Override
	public void render(double time) {
		GL11.glPushMatrix();

		Matrix4d temp = new Matrix4d(matrix.get(time));
		GL11.glMultMatrixf(temp.get(new float[16]));

		if (!visible) {
			return;
		}

		GL11.glPointSize(pointSize);
		if (colorFill != null) {
			OGLWrapper.glColor(colorFill);
		}

		GL11.glBegin(GL11.GL_POINTS);
		for (int i = 0; i < positions.size(); i++) {
			Vector3d point = positions.get(i);

			if (colorFill == null) {
				OGLWrapper.glColor(colors.get(i));
			}
			GL11.glVertex3d(point.x, point.y, point.z);

		}

		GL11.glEnd();

		GL11.glPopMatrix();

	}


	public void render2d() {
		if (!visible)
			return;
		GL11.glPointSize(pointSize);
		if (colorFill != null) {
			OGLWrapper.glColor(colorFill);
		}

		GL11.glBegin(GL11.GL_POINTS);
		for (int i = 0; i < positions.size(); i++) {
			Vector3d point = positions.get(i);

			if (colorFill == null) {
				Color pointColor = colors.get(i);
				OGLWrapper.glColor(pointColor);
			}
			GL11.glVertex2d(point.x, point.y);

		}

		GL11.glEnd();
		GL11.glPointSize(1);
	}


	public void render2d(Color colorOverride) {
		if (!visible) {
			return;
		}

		GL11.glPointSize(pointSize);
		OGLWrapper.glColor(colorOverride);

		GL11.glBegin(GL11.GL_POINTS);
		for (int i = 0; i < positions.size(); i++) {
			Vector3d point = positions.get(i);
			GL11.glVertex2d(point.x, point.y);
		}

		GL11.glEnd();
		GL11.glPointSize(1);
	}


	public void addPoint(Vector3d point) {
		positions.add(point);
	}


	public void addPoint(Vector3d point, Color color) {
		positions.add(point);
		colors.add(color);
		colorFill = null;
	}


	public void addPoint(Vector2d point, Color color) {
		positions.add(new Vector3d(point, 0));
		colors.add(color);
		colorFill = null;
	}


	/**
	 * Returns a BufferedImage of the requested dimensions with the cloud drawn to
	 * it Current this is a very 'dumb' drawing, and blank pixels will be ignored
	 * Meant to be used with 2d, normalized clouds
	 */

	public BufferedImage toBufferedImage(int width, int height, boolean normalized) {
		BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < width * height; i++) {
			int x = i % width;
			int y = i / width;
			out.setRGB(x, y, 0xFF000000);
		}

		for (int i = 0; i < positions.size(); i++) {
			// Color c = (colorFill != null) ? colorFill : colors.get(i);
			Color c = colors.get(i);
			int x = (int) (positions.get(i).x * (normalized ? width : 1));
			int y = (int) (positions.get(i).y * (normalized ? height : 1));
			try {
				out.setRGB(x, y, c.toInt());
			} catch (ArrayIndexOutOfBoundsException e) {
				System.out.println(e + " : " + x + " : " + y);
			}

		}

		return (out);
	}


	@Override
	public Vector3d[] getVectorRepresentation(double resolution) {
		System.out.println("Point Cloud Does Not Have a Vector Representation");
		return null;
	}


	@Override
	public ArrayList<Line> getHatchLines() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void recalculateExplicitGeometry() {
		explicitGeometry = this;
	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}
}
