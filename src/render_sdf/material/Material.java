package render_sdf.material;

import utility.Color;
import utility.VectorD;

public class Material {
	public Color diffuseColor;
	
	public double reflectivity;
	
	public Material(Color diffuseColor, double reflectivity) {
		this.diffuseColor = diffuseColor;
		this.reflectivity = reflectivity;
	
	}
}
