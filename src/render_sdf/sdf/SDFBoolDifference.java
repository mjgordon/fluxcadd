package render_sdf.sdf;

import render_sdf.renderer.Content_Renderer;
import utility.PVectorD;

public class SDFBoolDifference extends SDF {
	private SDF a;
	private SDF b;
	
	public SDFBoolDifference(SDF a, SDF b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public double getDistance(PVectorD v) {
		double aD = a.getDistance(v);
		double bD = b.getDistance(v);
		/*
		if (aD > -bD) {
			Content_Renderer.hitColor = 0xFFFF0000;
		}
		else {
			Content_Renderer.hitColor = 0xFF0000FF;
		}
		*/
		return (Math.max(aD, -bD));
	}
	
}
