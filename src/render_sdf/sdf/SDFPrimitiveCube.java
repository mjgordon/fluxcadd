package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveCube extends SDF {
	private Matrix4d frame;
	private Matrix4d frameInvert;
	private double halfSize;


	public SDFPrimitiveCube(Vector3d position, double size, Material material) {
		this.frame = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		this.frameInvert = new Matrix4d(frame).invert();
		this.halfSize = size / 2;
		this.material = material;
	}


	public SDFPrimitiveCube(Matrix4d frame, double size, Material material) {
		this.frame = frame;
		this.frameInvert = new Matrix4d(frame).invert();
		this.halfSize = size / 2;
		this.material = material;
	}


	@Override
	public DistanceData getDistance(Vector3d v) {
		Vector3d vLocal = frameInvert.transformPosition(v, new Vector3d());
		return (new DistanceData(Math.max(Math.abs(vLocal.x), Math.max(Math.abs(vLocal.y), Math.abs(vLocal.z))) - halfSize, this.material));
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		Group g = new Group();

		Color c = solid ? previewColorSolid : previewColorVoid;
		
		if (materialPreview) {
			c =  this.material.diffuseColor;
		}

		g.add(new Line(new Vector3d(-halfSize, -halfSize, -halfSize), new Vector3d(halfSize, -halfSize, -halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(-halfSize, halfSize, -halfSize), new Vector3d(halfSize, halfSize, -halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(-halfSize, -halfSize, halfSize), new Vector3d(halfSize, -halfSize, halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(-halfSize, halfSize, halfSize), new Vector3d(halfSize, halfSize, halfSize)).setFillColor(c));

		g.add(new Line(new Vector3d(-halfSize, -halfSize, -halfSize), new Vector3d(-halfSize, halfSize, -halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(halfSize, -halfSize, -halfSize), new Vector3d(halfSize, halfSize, -halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(-halfSize, -halfSize, halfSize), new Vector3d(-halfSize, halfSize, halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(halfSize, -halfSize, halfSize), new Vector3d(halfSize, halfSize, halfSize)).setFillColor(c));

		g.add(new Line(new Vector3d(-halfSize, -halfSize, -halfSize), new Vector3d(-halfSize, -halfSize, halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(halfSize, -halfSize, -halfSize), new Vector3d(halfSize, -halfSize, halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(-halfSize, halfSize, -halfSize), new Vector3d(-halfSize, halfSize, halfSize)).setFillColor(c));
		g.add(new Line(new Vector3d(halfSize, halfSize, -halfSize), new Vector3d(halfSize, halfSize, halfSize)).setFillColor(c));

		g.setFrame(frame);

		gd.add(g);
	}
}
