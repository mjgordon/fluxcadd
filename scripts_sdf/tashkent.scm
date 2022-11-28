(let* ((material-ground (Material. (Color. "FAC748") 0.0))
       (material-building (Material. (Color. "8390FA") 0.0)))
       

  (set-camera-position 42.0 7.0 1.0)
  (set-camera-target -0.5 1.4 18.0)
  
  (set-scene-sdf (SDFPrimitiveGroundPlane. 0 material-ground))

  (let* ((block (SDFBoolIntersection. (SDFBoolDifference. (SDFOpSubtract. (SDFPrimitiveCube. (Vector3d. 0.0 0.0 20.0)
											     10.0
											     material-building)
									  1.0)
							  (SDFOpSubtract. (SDFPrimitiveCube. (Vector3d. 0.0 0.0 20.0)
											     10.0
											     material-building)
									  0.9))
				      (SDFPrimitiveCube. (Vector3d. 0.0 0.0 20.0) 8.0 20.0 20.0 material-building))))

    (set-scene-sdf (SDFBoolUnion. scene-sdf block))))







