(let* ((material-main (Material. (Color. 255 0 0) 0))
       (material-carve (Material. (Color. 0 0 255) 0))
       (material-reflect (Material. (Color. 255 255 255) 1)))

  (set-camera-position 100.0 0.0 30.0)
  (set-camera-target 0.0 0.0 -10.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-main))

  (with-sdf scene-sdf
	    (bool-difference (SDFPrimitiveSphere. (Vector3d. 0.0 0.0 0.0) 30 material-carve))
	    (bool-difference (SDFPrimitiveSphere. (Vector3d. -35.0 0.0 0.0) 20 material-carve))
	    (bool-difference (SDFPrimitiveSphere. (Vector3d. 35.0 0.0 0.0) 20 material-carve))
	    (bool-difference (SDFPrimitiveSphere. (Vector3d. 70.0 0.0 0.0) 20 material-carve))
	    (bool-difference (SDFPrimitiveSphere. (Vector3d. 105.0 0.0 0.0) 20 material-carve))

	    (bool-union (SDFPrimitiveCube. (Vector3d. 0.0 20.0 10.0) 10.0 material-main))
	    (op-chamfer (SDFPrimitiveSphere. (Vector3d. 0.0 25.0 15.0) 5 material-carve) 1.0)
	    
	    (bool-union (SDFPrimitiveCube. (Vector3d. 0.0 -20.0 10.0) 10.0 material-main))
	    (op-smooth (SDFPrimitiveSphere. (Vector3d. 0.0 -25.0 15.0) 5 material-carve) 1.0)

	    (bool-union (SDFPrimitiveSphere. (Vector3d. -30.0 0.0 15.0) 10.0 material-reflect))))
