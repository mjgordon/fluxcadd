package render_sdf.material;

import org.joml.Vector3d;

import utility.Color3i;

public class MaterialDiffuse extends Material {
	
	private Color3i diffuseColor;

	private double reflectivity;
	
	public MaterialDiffuse(Color3i diffuseColor, double reflectivity) {
		this.diffuseColor = diffuseColor;
		this.reflectivity = reflectivity;
	}

	@Override
	public Material getMaterial(Vector3d v, double time) {
		return new MaterialDiffuse(diffuseColor.copy(), reflectivity);
	}

	@Override
	public Color3i getColor() {
		return diffuseColor.copy();
	}

	@Override
	public double getReflectivity() {
		return reflectivity;
	}
}
