package geometry;

import java.util.ArrayList;
import java.util.Arrays;

import org.joml.Vector3d;
import org.lwjgl.opengl.GL11;

import intersection.Intersection;

public class Group extends Geometry {

	private ArrayList<Geometry> geometry;


	public Group() {
		geometry = new ArrayList<Geometry>();
	}


	public Group(ArrayList<Geometry> geometry) {
		this.geometry = geometry;
	}


	public void add(Geometry g) {
		geometry.add(g);
	}


	public Geometry getChild(int id) {
		return geometry.get(id);
	}


	@Override
	public void render() {
		if (visible) {
			GL11.glPushMatrix();

			GL11.glMultMatrixd(frame.get(new double[16]));

			for (Geometry g : geometry) {
				g.render();
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
