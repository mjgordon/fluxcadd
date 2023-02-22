package render_sdf.sdf;

import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector4d;

import geometry.GeometryDatabase;

public class SDFOpTranslate extends SDF {
	private Matrix4d frame;
	private Matrix4d frameInvert;
	
	private SDF child;
	
	public SDFOpTranslate(SDF child, Vector3d position) {
		this.frame = new Matrix4d().setColumn(3, new Vector4d(position, 1));
		this.frameInvert = new Matrix4d(frame).invert();
		this.child = child;
	}

	@Override
	public DistanceData getDistance(Vector3d v, double time) {
		Vector3d vLocal = frameInvert.transformPosition(v, new Vector3d());
		return child.getDistance(vLocal, time);
	}

	@Override
	public void extractSceneGeometry(GeometryDatabase gd, boolean solid, boolean materialPreview) {
		child.extractSceneGeometry(gd, solid, materialPreview);
	}
	
	@Override
	public String describeTree(String input, int depth, String spacer) {
		input = super.describeTree(input, depth, spacer);
		input += "OpTranslate";
		input = child.describeTree(input, depth + 1, PIPE_ELBOW);
		return input;
	}

}
