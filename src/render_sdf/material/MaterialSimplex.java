package render_sdf.material;

import org.joml.Vector3d;

import utility.Color;
import utility.OpenSimplexNoise;
import utility.math.UtilMath;

public class MaterialSimplex extends Material {
	
	OpenSimplexNoise simplex;
	
	private Color diffuseA;
	private double reflectivityA;
	
	private Color diffuseB;
	private double reflectivityB;
	
	private double scale;
	
	private Color cachedColor = null;
	private double cachedReflectivity = 0;
	
	public MaterialSimplex(Color diffuseA, double reflectivityA, Color diffuseB, double reflectivityB, double scale) {
		this.diffuseA = diffuseA;
		this.reflectivityA = reflectivityA;
		
		this.diffuseB = diffuseB;
		this.reflectivityB = reflectivityB;	
		
		simplex = new OpenSimplexNoise();
		
		this.scale = scale;
		
		cachedColor = diffuseA;
		cachedReflectivity = reflectivityA;
	}
	
	@Override
	public Material getMaterial(Vector3d v, double time) {
		double factor = simplex.eval(v.x * scale, v.y * scale, v.z * scale, time) * 0.5 + 0.5;
		cachedColor = Color.lerpColor(diffuseA, diffuseB, factor);
		cachedReflectivity = UtilMath.lerp(reflectivityA, reflectivityB, factor);
		return new MaterialDiffuse(cachedColor, cachedReflectivity);	
	}

	@Override
	public Color getColor() {
		return cachedColor;
	}

	@Override
	public double getReflectivity() {
		return cachedReflectivity;
	}
}
