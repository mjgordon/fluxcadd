package intersection;

import org.joml.Vector2d;
import org.joml.Vector3d;

public class Intersection {
	public Vector3d realPosition;
	public Vector2d uv;
	public int faceId;


	public Intersection(Vector3d realPosition, Vector2d uv) {
		this.realPosition = realPosition;
		this.uv = uv;
		this.faceId = -1;
	}


	public Intersection(Vector3d realPosition, Vector2d uv, int faceId) {
		this.realPosition = realPosition;
		this.uv = uv;
		this.faceId = faceId;
	}
}
