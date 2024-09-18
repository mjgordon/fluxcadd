package render_sdf.material;

import org.joml.Vector3d;

import utility.Color;

public class MaterialDiffuse extends Material {
	
	private Color diffuseColor;

	private double reflectivity;
	
	public MaterialDiffuse(Color diffuseColor, double reflectivity) {
		this.diffuseColor = diffuseColor;
		this.reflectivity = reflectivity;
	}

	@Override
	public Material getMaterial(Vector3d v, double time) {
		return new MaterialDiffuse(diffuseColor.copy(), reflectivity);
	}

	@Override
	public Color getColor() {
		return diffuseColor.copy();
	}

	@Override
	public double getReflectivity() {
		return reflectivity;
	}
}
