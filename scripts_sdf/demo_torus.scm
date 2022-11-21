(let* ((material-ground (Material. (Color. "444455") 0))
       (material-torus (Material. (Color. "EEEEDD") 0))
       (material-sphere (Material. (Color. "FF0000") 0)))

  (set-camera-position -70.0 46.0 170.0)
  (set-camera-target 1.0 10.0 42.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (with-sdf scene-sdf
	    (bool-union (SDFPrimitiveTorus. (.m32 (Matrix4d.) 20.0) 50.0 10.0 material-torus))
	    (bool-union (SDFPrimitiveCross. (Vector3d. 0.0 0.0 0.0) 1.0 material-torus))

	    (bool-union (SDFPrimitiveCross. (Vector3d. 0.0 0.0 10.0) 2.0 material-sphere))
	    (bool-union (SDFPrimitiveCross. (Vector3d. 10.0 0.0 10.0) 2.0 material-sphere))
	    (bool-union (SDFPrimitiveCross. (Vector3d. 20.0 0.0 10.0) 2.0 material-sphere))
	    (bool-union (SDFPrimitiveCross. (Vector3d. 30.0 0.0 10.0) 2.0 material-sphere))
	    (bool-union (SDFPrimitiveCross. (Vector3d. 40.0 0.0 10.0) 2.0 material-sphere))
	    (bool-union (SDFPrimitiveCross. (Vector3d. 50.0 0.0 10.0) 2.0 material-sphere))))



