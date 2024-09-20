package geometry;

import java.util.ArrayList;
import java.util.Arrays;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import intersection.Intersection;
import render_sdf.animation.Matrix4dAnimated;

public class Group extends Geometry {

	private ArrayList<Geometry> geometry;


	public Group() {
		geometry = new ArrayList<Geometry>();
		setMatrix(new Matrix4dAnimated(new Matrix4d(),"Group"));
	}


	public Group(ArrayList<Geometry> geometry) {
		this.geometry = geometry;
		setMatrix(new Matrix4dAnimated(new Matrix4d(),"Group"));
	}


	public void add(Geometry g) {
		geometry.add(g);
	}


	public Geometry getChild(int id) {
		return geometry.get(id);
	}


	@Override
	public void render(double time) {
		if (visible) {
			GL11.glPushMatrix();
			
			GL11.glMultMatrixd(matrix.getArray(time));

			for (Geometry g : geometry) {
				g.render(time);
			}

			GL11.glPopMatrix();
		}
	}


	@Override
	public Vector3d[] getVectorRepresentation(double resolution) {
		ArrayList<Vector3d> out = new ArrayList<Vector3d>();
		for (Geometry g : geometry) {
			out.addAll(Arrays.asList(g.getVectorRepresentation(resolution)));
		}
		return out.toArray(new Vector3d[out.size()]);
	}


	@Override
	public ArrayList<Line> getHatchLines() {
		ArrayList<Line> out = new ArrayList<Line>();
		for (Geometry g : geometry) {
			out.addAll(g.getHatchLines());
		}
		return out;
	}


	@Override
	public void recalculateExplicitGeometry() {
		// TODO Auto-generated method stub

	}


	@Override
	public Intersection intersectLine(Vector3d start, Vector3d end) {
		// TODO Auto-generated method stub
		return null;
	}
}
