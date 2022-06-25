package render_sdf.material;

import utility.Color;
import utility.Util;

public class Material {
	public Color diffuseColor;
	
	public double reflectivity;
	
	public Material(Color diffuseColor, double reflectivity) {
		this.diffuseColor = diffuseColor;
		this.reflectivity = reflectivity;
	
	}
	
	public static Material lerpMaterial(Material a, Material b, double factor) {
		Color color = Color.lerpColor(a.diffuseColor, b.diffuseColor, factor);
		double reflectivity = Util.lerp(a.reflectivity, b.reflectivity, factor);
		
		return(new Material(color,reflectivity));
	}
}
