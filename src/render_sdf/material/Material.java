package render_sdf.material;

import utility.Color;
import utility.PVectorD;

public class Material {
	public Color diffuseColor;
	
	public double reflectivity;
	
	public Material(Color diffuseColor, double reflectivity) {
		this.diffuseColor = diffuseColor;
		this.reflectivity = reflectivity;
	
	}
}
