package render_sdf.sdf;

import java.util.ArrayList;

import org.joml.Vector3d;

import geometry.GeometryDatabase;
import render_sdf.animation.Animated;
import render_sdf.material.Material;
import render_sdf.renderer.VectorContext;

public class SDFBoolUnion extends SDF {

	public SDFBoolUnion(SDF a, SDF b) {
		this.childA = a;
		this.childB = b;

		displayName = "BoolUnion";
	}


	public SDFBoolUnion(SDF start) {
		children = new ArrayList<SDF>();

		children.add(start);

		displayName = "BoolUnion";
	}


	public void addChild(SDF child) {
		children.add(child);
	}


	@Override
	public double getDistance(Vector3d v, double time, VectorContext context) {
		if (children == null) {
			double ad = childA.getDistance(v, time, context);
			double bd = childB.getDistance(v, time, context);

			return Math.min(ad, bd);
		}
		else {
			double bestDistance = Double.MAX_VALUE;

			for (SDF child : children) {
				double childDistance = child.getDistance(v, time, context);
				if (childDistance < bestDistance) {
					bestDistance = childDistance;
				}
			}

			return bestDistance;
		}
	}


	@Override
	public Material getMaterial(Vector3d v, double time, VectorContext context) {
		if (children == null) {
			double ad = childA.getDistance(v, time, context);
			double bd = childB.getDistance(v, time, context);

			return ad < bd ? childA.getMaterial(v, time, context) : childB.getMaterial(v, time, context);
		}
		else {
			SDF bestChild = null;
			double bestDistance = Double.MAX_VALUE;

			for (SDF child : children) {
				double childDistance = child.getDistance(v, time, context);
				if (bestChild == null || childDistance < bestDistance) {
					bestChild = child;
					bestDistance = childDistance;
				}
			}

			return bestChild.getMaterial(v, time, context);
		}
	}


	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview, double time) {
		if (children == null) {
			childA.extractSceneGeometry(gd, solid, materialPreview, time);
			childB.extractSceneGeometry(gd, solid, materialPreview, time);
		}
		else {
			for (SDF child : children) {
				child.extractSceneGeometry(gd, solid, materialPreview, time);
			}
		}
	}


	@Override
	public Animated[] getAnimated() {
		return null;
	}
	
	
	@Override
	public String getSourceRepresentation(ArrayList<String> definitions, ArrayList<String> functions,ArrayList<String> transforms, String vLocalLast, double time) {
		String compStringA = childA.getSourceRepresentation(definitions, functions, transforms, vLocalLast, time);
		String compStringB = childB.getSourceRepresentation(definitions, functions, transforms, vLocalLast, time);
		
		return "Math.min(" + compStringA + "," + compStringB + ")";
	}

}
