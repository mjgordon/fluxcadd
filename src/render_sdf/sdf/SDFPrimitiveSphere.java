package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;
import geometry.Group;
import geometry.Polyline;
import render_sdf.animation.Animated;
import render_sdf.animation.Matrix4dAnimated;
import render_sdf.material.Material;
import utility.Color;
import utility.math.UtilMath;

public class SDFPrimitiveSphere extends SDF {
	private Matrix4dAnimated frame;
	private double radius;


	public SDFPrimitiveSphere(Vector3d position, double radius, Material material) {
		Matrix4d base = new Matrix4d().setColumn(3,new Vector4d(position,1));
		frame = new Matrix4dAnimated(base, "Sphere");
		this.radius = radius;
		this.material = material;
		
		displayName = "PrimSphere";
	}


	@Override
	public double getDistance(Vector3d v, double time) {
		Vector3d vLocal = v.mulPosition(frame.getInvert(time), new Vector3d());
		return vLocal.length() - radius;
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		Group g = new Group();

		int segments = 36;

		Vector3d[] verticesX = new Vector3d[segments + 1];
		Vector3d[] verticesY = new Vector3d[segments + 1];
		Vector3d[] verticesZ = new Vector3d[segments + 1];

		for (int i = 0; i <= segments; i++) {
			double n = 1.0f * i / segments * UtilMath.TWO_PI;

			double cosVal = Math.cos(n) * radius;

			double sinVal = Math.sin(n) * radius;

			verticesX[i] = new Vector3d(0, cosVal, sinVal);
			verticesY[i] = new Vector3d(cosVal, 0, sinVal);
			verticesZ[i] = new Vector3d(cosVal, sinVal, 0);
		}

		Color c = getPrimitiveColor(solid, materialPreview);

		g.add(new Polyline(verticesX).setFillColor(c));
		g.add(new Polyline(verticesY).setFillColor(c));
		g.add(new Polyline(verticesZ).setFillColor(c));

		g.setMatrix(frame);

		gd.add(g);
	}
	
	@Override
	public Animated[] getAnimated() {
		return new Animated[] {frame};
	}
}
