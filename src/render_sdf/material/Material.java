package render_sdf.material;

import org.joml.Vector3d;

import utility.Color;
import utility.math.UtilMath;

public abstract class Material {

	public static Material lerpMaterial(Material a, Material b, double factor) {
		Color color = Color.lerpColor(a.getColor(), b.getColor(), factor);
		double reflectivity = UtilMath.lerp(a.getReflectivity(), b.getReflectivity(), factor);

		return (new MaterialDiffuse(color, reflectivity));
	}
	
	public Material lerpTowards(Color c, double factor) {
		return new MaterialDiffuse(Color.lerpColor(getColor(), c, factor),getReflectivity());
	}

	public abstract Material getMaterial(Vector3d v, double time);
	
	public abstract Color getColor();
	
	public abstract double getReflectivity();
}
