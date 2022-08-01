package render_sdf.raytracer;

import org.joml.Vector3d;

import render_sdf.material.Material;
import utility.Color;

public class Sphere extends RenderGeometry {
	public Vector3d position;
	public double radius;
	public double radius2;


	public Sphere(Vector3d position, double radius, Color diffuseColor) {
		this.position = position;
		this.radius = radius;
		this.radius2 = radius * radius;
		this.material = new Material(diffuseColor, 0);
	}


	// Had some mouse integration, not currently sure how to remove
	@Override
	public Vector3d intersect(Vector3d origin, Vector3d direction) {
		/*
		 * VectorD l = VectorD.sub(position, origin); double tca = VectorD.dot(l,
		 * direction);
		 * 
		 * if (tca < 0) return (null); double d2 = VectorD.dot(l, l) - tca * tca;
		 * 
		 * if (false) { double mouseDiff = (10.0f * p.mouseX / p.width); double mod =
		 * (Math.sin(mouseDiff * Renderer.currentY / p.width + (1.0f * p.mouseY /
		 * p.height)) + 1) * 300; d2 %= mod; }
		 * 
		 * if (d2 > radius2) return (null); double thc = (float) Math.sqrt(radius2 -
		 * d2);
		 * 
		 * double t0 = tca - thc; double t1 = tca + thc;
		 * 
		 * if (t0 < 0) t0 = t1;
		 * 
		 * VectorD out = VectorD.add(origin, VectorD.mult(direction, t0));
		 * 
		 * return (out);
		 */
		return new Vector3d(0, 0, 0);
	}


	public Vector3d getNormal(Vector3d input) {
		return (new Vector3d(input).sub(position).normalize());
	}
}