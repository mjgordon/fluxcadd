package render_sdf.material;

import org.joml.Vector3d;

import utility.Color3i;
import utility.math.UtilMath;

public abstract class Material {

	public static Material lerpMaterial(Material a, Material b, double factor) {
		Color3i color = Color3i.lerpColor(a.getColor(), b.getColor(), factor);
		double reflectivity = UtilMath.lerp(a.getReflectivity(), b.getReflectivity(), factor);

		return (new MaterialDiffuse(color, reflectivity));
	}
	
	public Material lerpTowards(Color3i c, double factor) {
		return new MaterialDiffuse(Color3i.lerpColor(getColor(), c, factor),getReflectivity());
	}

	public abstract Material getMaterial(Vector3d v, double time);
	
	public abstract Color3i getColor();
	
	public abstract double getReflectivity();
}
