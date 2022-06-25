package render_sdf.sdf;

import render_sdf.material.Material;

public class DistanceData {
	public double distance;
	public Material material;
	
	public DistanceData(double distance, Material material) {
		this.distance = distance;
		this.material = material;
	}
}
