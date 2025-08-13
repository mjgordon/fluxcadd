package render_sdf.material;

import org.joml.SimplexNoise;
import org.joml.Vector3d;

import utility.Color3i;
import utility.math.UtilMath;

public class MaterialSimplex extends Material {
	
	private Color3i diffuseA;
	private double reflectivityA;
	
	private Color3i diffuseB;
	private double reflectivityB;
	
	private double scale;
	
	private Color3i cachedColor = null;
	private double cachedReflectivity = 0;
	
	public MaterialSimplex(Color3i diffuseA, double reflectivityA, Color3i diffuseB, double reflectivityB, double scale) {
		this.diffuseA = diffuseA;
		this.reflectivityA = reflectivityA;
		
		this.diffuseB = diffuseB;
		this.reflectivityB = reflectivityB;	
		
		this.scale = scale;
		
		cachedColor = diffuseA;
		cachedReflectivity = reflectivityA;
	}
	
	@Override
	public Material getMaterial(Vector3d v, double time) {
		double factor = SimplexNoise.noise((float)(v.x * scale),(float)( v.y * scale),(float)( v.z * scale),(float) time) * 0.5 + 0.5;
		cachedColor = Color3i.lerpColor(diffuseA, diffuseB, factor);
		cachedReflectivity = UtilMath.lerp(reflectivityA, reflectivityB, factor);
		return new MaterialDiffuse(cachedColor, cachedReflectivity);	
	}

	@Override
	public Color3i getColor() {
		return cachedColor;
	}

	@Override
	public double getReflectivity() {
		return cachedReflectivity;
	}
}
