package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Line;
import render_sdf.material.Material;
import utility.Color;

public class SDFPrimitiveCross extends SDF {
	private Matrix4d frame;
	private Matrix4d frameInvert;
	private double size;
	private double halfSize;
	private double axisSize;

	private double previewSize = 300;


	public SDFPrimitiveCross(Vector3d position, double size, Material material) {
		this.frame = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		this.frameInvert = frame.invert(new Matrix4d());
		this.size = size;
		this.halfSize = size / 2;
		this.axisSize = Math.sqrt(Math.pow(size, 2) / 2);
		this.material = material;
	}


	public SDFPrimitiveCross(Matrix4d frame, double size, Material material) {
		this.frame = frame;
		this.frameInvert = frame.invert(new Matrix4d());
		this.size = size;
		this.halfSize = size / 2;
		this.axisSize = Math.sqrt(Math.pow(size, 2) / 2);
		this.material = material;
	}


	@Override
	public DistanceData getDistance(Vector3d v) {

		Vector3d vLocal = new Vector3d(v).mulPosition(frameInvert);

		double ax = Math.abs(vLocal.x);
		double ay = Math.abs(vLocal.y);
		double az = Math.abs(vLocal.z);

		double distance;

		if (ax <= az && ay <= az) {
			distance = calc2d(ax, ay);
		}
		else if (ax <= ay && az <= ay) {
			distance = calc2d(ax, az);
		}
		else {
			distance = calc2d(ay, az);
		}

		return (new DistanceData(distance, this.material));
	}


	private double calc2d(double a, double b) {
		if (a >= (b - axisSize) && a <= (b + axisSize)) {
			double c = a + b;
			return (Math.sqrt(Math.pow(c, 2) / 2) - halfSize);
		}
		else {
			if (a < b) {
				return (Math.sqrt(Math.pow(a, 2) + Math.pow(b - axisSize, 2)));	
			}
			else {
				return (Math.sqrt(Math.pow(a - axisSize, 2) + Math.pow(b, 2)));
			}
			
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		Group g = new Group();

		double hp = previewSize / 2;

		Color c = getPrimitiveColor(solid, materialPreview);

		g.add(new Line(new Vector3d(-hp, 0, 0), new Vector3d(hp, 0, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, -hp, 0), new Vector3d(0, hp, 0)).setFillColor(c));
		g.add(new Line(new Vector3d(0, 0, -hp), new Vector3d(0, 0, hp)).setFillColor(c));

		g.setFrame(frame);

		gd.add(g);
	}
}
