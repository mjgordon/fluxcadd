(let* ((material-ground (Material. (Color. "FAC748") 0.0))
       (material-star (Material. (Color. "8390FA") 1.0))
       (material-sphere (Material. (Color. "555555") 1.0)))

  (set-camera-position 42.0 7.0 1.0)
  (set-camera-target -0.5 1.4 18.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (with-sdf scene-sdf
	    (bool-union (SDFPrimitiveStar. (Vector3d. 0.0 0.0 20.0) 40.0 material-star))
	    (bool-union (SDFOpSubtract. (SDFOpModulo. (SDFPrimitiveSphere. (Vector3d. 5.0 5.0 5.0)
									   2.0
									   material-sphere)
						      10.0)
					(SDFPrimitiveSimplex. material-ground 1.0)
					0.5))))





