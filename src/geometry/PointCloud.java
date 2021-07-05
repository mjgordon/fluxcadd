package geometry;

import org.lwjgl.opengl.GL11;

import graphics.OGLWrapper;

import java.util.ArrayList;

import utility.Color;
import utility.PMatrix3D;
import utility.PVector;
import utility.Util;

public class PointCloud extends Geometry {

	public ArrayList<PVector> positions;
	public ArrayList<Color> colors;
	public ArrayList<PVector> normals;
	
	public int pointSize = 1;

	public PointCloud() {
		positions = new ArrayList<PVector>();
		colors = new ArrayList<Color>();
		normals = new ArrayList<PVector>();

		colorFill = new Color(0,0,0);

	}

	public PointCloud(String filepath) {
		String[] input = Util.loadStringsFromFile(filepath);

		positions = new ArrayList<PVector>();
		colors = new ArrayList<Color>();
		normals = new ArrayList<PVector>();

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

			positions.add(new PVector(x, y, z));
			colors.add(new Color(r, g, b));
			normals.add(new PVector(u, v, w));
		}

		colorFill = null;
	}

	@Override
	public void render() {
		GL11.glPushMatrix();
		
		PMatrix3D temp = frame.get();
		temp.transpose();
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
			PVector point = positions.get(i);


			if (colorFill == null) {
				OGLWrapper.glColor(colors.get(i));
			}
			GL11.glVertex3f(point.x, point.y,point.z);

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
			PVector point = positions.get(i);

			if (colorFill == null) {
				Color pointColor = colors.get(i);
				OGLWrapper.glColor(pointColor);
			}
			GL11.glVertex2f(point.x, point.y);

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
			PVector point = positions.get(i);
			GL11.glVertex2f(point.x, point.y);
		}

		GL11.glEnd();
		GL11.glPointSize(1);
	}
	
	public void addPoint(PVector point) {
		positions.add(point);
	}
	
	public void addPoint(PVector point, Color color) {
		positions.add(point);
		colors.add(color);
		colorFill = null;
	}

	@Override
	public PVector[] getVectorRepresentation(float resolution) {
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

}
