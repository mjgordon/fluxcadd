package intersection;

import utility.PVector;

public class Intersection {
	public PVector realPosition;
	public PVector uv;
	public int faceId;
	
	public Intersection(PVector realPosition,PVector uv) {
		this.realPosition = realPosition;
		this.uv = uv;
		this.faceId = -1;
	}
	
	public Intersection(PVector realPosition,PVector uv,int faceId) {
		this.realPosition = realPosition;
		this.uv = uv;
		this.faceId = faceId;
	}
}
